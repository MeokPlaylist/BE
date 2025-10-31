package com.meokplaylist.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meokplaylist.api.dto.KakaoSearchResponse;
import com.meokplaylist.api.dto.SlicedResponse;
import com.meokplaylist.api.dto.UrlMappedByFeedIdDto;
import com.meokplaylist.api.dto.UserPageDto;
import com.meokplaylist.api.dto.feed.FeedRegionMappingDto;
import com.meokplaylist.api.dto.socialInteraction.*;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.domain.repository.category.CategoryRepository;
import com.meokplaylist.domain.repository.category.LocalCategoryRepository;
import com.meokplaylist.domain.repository.category.UserLocalCategoryRepository;
import com.meokplaylist.domain.repository.feed.FeedPhotosRepository;
import com.meokplaylist.domain.repository.feed.FeedRepository;
import com.meokplaylist.domain.repository.place.PlacesRepository;
import com.meokplaylist.domain.repository.socialInteraction.CommentsRepository;
import com.meokplaylist.domain.repository.socialInteraction.FollowsRepository;
import com.meokplaylist.domain.repository.socialInteraction.FavoritePlaceRepository;
import com.meokplaylist.domain.repository.socialInteraction.LikesRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.category.Category;
import com.meokplaylist.infra.category.LocalCategory;
import com.meokplaylist.infra.category.UserLocalCategory;
import com.meokplaylist.infra.feed.Feed;
import com.meokplaylist.infra.feed.FeedPhotos;
import com.meokplaylist.infra.place.Places;
import com.meokplaylist.infra.place.FavoritePlace;
import com.meokplaylist.infra.socialInteraction.Comments;
import com.meokplaylist.infra.socialInteraction.Follows;
import com.meokplaylist.infra.socialInteraction.Likes;
import com.meokplaylist.infra.user.Users;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SocialInteractionService {
    private final UsersRepository usersRepository;
    private final FollowsRepository followsRepository;
    private final UserLocalCategoryRepository userLocalCategoryRepository;
    private final FeedRepository feedRepository;
    private final PlacesRepository placesRepository;
    private final LikesRepository likesRepository;

    private final CommentsRepository commentsRepository;
    private final WebClient tourApiWebClient;
    private final FavoritePlaceRepository favoritePlaceRepository;
    private final LocalCategoryRepository localCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final FeedPhotosRepository feedPhotosRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final S3Service s3Service;
    private final PlaceService placeService;

    @Transactional
    public void follow(Long followingId, String followerNickname) {

        Users follower = usersRepository.findByNickname(followerNickname)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));
        Long followerId =follower.getUserId();

        if (followerId.equals(followingId)) {
            throw new BizExceptionHandler(ErrorCode.INVALID_INPUT); // 자기 자신 팔로우 금지
        }

        if (followsRepository.existsByFollowerUserIdAndFollowingUserId(followerId, followingId)) {
            return; // 이미 팔로우 상태면 무시
        }

        Users following = usersRepository.findByUserId(followingId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));


        Follows f = new Follows();
        f.setFollower(follower);
        f.setFollowing(following);
        followsRepository.save(f);
    }

    @Transactional
    public void unfollow(Long followingId, String unFollowerNickname) {

        Users follower = usersRepository.findByNickname(unFollowerNickname)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));
        Long followerId =follower.getUserId();

        followsRepository.deleteByFollowerUserIdAndFollowingUserId(followerId, followingId);
    }

    @Transactional(readOnly = true)
    public UserPageDto userPageDistinction(Long userId, String nickName){
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Boolean isMe=true;
        Boolean isFollowing=false;
        Boolean isFollower=false;

        if(!user.getNickname().equals(nickName)){
            Users me=user;

            isMe=false;
            user= usersRepository.findByNickname(nickName)
                    .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));
        }

        long feedNum = feedRepository.countByUserUserId(user.getUserId());
        long followingNum =followsRepository.countByFollowingUserId(user.getUserId());
        long followerNum =followsRepository.countByFollowerUserId(user.getUserId());
        String userNickname=user.getNickname();
        String userIntro=user.getIntroduction();
        String profileUrl=s3Service.generateGetPresignedUrl(user.getProfileImgKey());

        List<Object[]>  feedIdsGroupedByYear = feedRepository.findFeedIdsGroupedByYear(user.getUserId());
        List<FeedRegionMappingDto> feedIdsGroupedByRegion = feedRepository.findFeedIdsGroupedByRegion(user.getUserId());
        List<Long> feedIds = feedRepository.findFeedIdsByUserUserId(user.getUserId());
        List<UrlMappedByFeedIdDto> urlList = feedPhotosRepository.findByFeedFeedId(feedIds);

        Map<Long, String> urlMappedByFeedId = urlList.stream()
                .collect(Collectors.toMap(
                        UrlMappedByFeedIdDto::getFeedId, // 키: feedId를 추출합니다.
                        dto -> s3Service.generateGetPresignedUrl(dto.getFeedPhotos().getStorageKey()) // 값: DTO를 URL로 변환합니다.
                ));

        Map<Integer, List<Long>> feedIdsgroupedByYear = feedIdsGroupedByYear.stream()
                .collect(Collectors.groupingBy(
                        row -> (Integer) row[0],   // 연도
                        LinkedHashMap::new,        // 순서 유지
                        Collectors.mapping(row -> (Long) row[1], Collectors.toList())
                ));
        //feed별로 맨 앞 지역 하나만
        Map<Long, String> firstRegionByFeedId = feedIdsGroupedByRegion.stream()
                .collect(Collectors.toMap(
                        FeedRegionMappingDto::feedId,
                        dto -> dto.region() != null ? dto.region() : "기타",
                        (existing, replacement) -> existing
                ));
        //지역별 feed 매핑
        Map<String, List<Long>> feedIdsgroupedByRegion = firstRegionByFeedId.entrySet().stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue,
                        Collectors.mapping(Map.Entry::getKey,
                                Collectors.toList())
                ));


        UserPageDto userPageDto = UserPageDto.builder()
                .feedNum(feedNum)
                .followingNum(followingNum)
                .followerNum(followerNum)
                .userNickname(userNickname)
                .userIntro(userIntro)
                .profileUrl(profileUrl)
                .feedIdsGroupedByYear(feedIdsgroupedByYear)
                .feedIdsGroupedByRegion(feedIdsgroupedByRegion)
                .urlMappedByFeedId(urlMappedByFeedId)
                .isMe(isMe)
                .isFollowing(isFollowing)
                .isFollower(isFollower)
                .build();

        return userPageDto;
    }

    @Transactional(readOnly = true)
    public Mono<Map<String, List<String>>> initRecommendRestaurant(Long userId) {
        List<UserLocalCategory> userLocalCategories = userLocalCategoryRepository.findByUserUserId(userId);

        return Flux.fromIterable(userLocalCategories)
                .flatMap(userLocalCategory -> {
                    String regionName = userLocalCategory.getLocalCategory().getType() + ":" + userLocalCategory.getLocalCategory().getLocalName();
                    String areaCode = userLocalCategory.getLocalCategory().getAreaCode().toString();
                    String sigunguCode = userLocalCategory.getLocalCategory().getSigunguCode().toString();

                    return tourApiWebClient.get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/areaBasedList1")
                                    .queryParam("areaCd", areaCode)
                                    .queryParam("signguCd", sigunguCode)
                                    .queryParam("numOfRows", 150)
                                    .queryParam("baseYm", "202503")
                                    .build())
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMapMany(jsonString -> {
                                try {
                                    JsonNode root = objectMapper.readTree(jsonString);
                                    JsonNode items = root.path("response").path("body").path("items").path("item");

                                    List<String> restaurantNames = new ArrayList<>();
                                    if (items.isArray()) {
                                        for (JsonNode item : items) {
                                            if ("음식".equals(item.path("rlteCtgryLclsNm").asText())) {
                                                restaurantNames.add(item.path("rlteTatsNm").asText());
                                            }
                                        }
                                    }

                                    // ⚡ 핵심: 지역별로 식당 "하나씩" 흘려보내기
                                    return Flux.fromIterable(restaurantNames)
                                            .map(name -> new AbstractMap.SimpleEntry<>(regionName, name));

                                } catch (Exception e) {
                                    return Flux.error(new RuntimeException("JSON parsing error", e));
                                }
                            });
                })
                .collectMultimap( // 같은 키에 값들을 자동으로 List로 묶음
                        Map.Entry::getKey,  // regionName
                        Map.Entry::getValue // restaurantName (String)
                )
                .map(m -> m.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> new ArrayList<>(e.getValue()) // 필요시 ArrayList<String>으로 변환
                        ))
                );
    }

    @Transactional(readOnly = true)
    public Mono<Map<String, List<String>>> recommendRestaurant(RecommendRestaurantRequest request) {

        List<String> regions = request.getRegions();

        return Flux.fromIterable(Objects.requireNonNullElse(regions, Collections.emptyList()))
                // 1. 지역 문자열 파싱 후 LocalCategory 조회
                .flatMap(raw -> {
                    String[] parts = raw.split(":", 2);
                    if (parts.length != 2) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);
                    String type = parts[0].trim();
                    String name = parts[1].trim();
                    if (type.isEmpty() || name.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);

                    List<LocalCategory> categories = localCategoryRepository.findAllByTypeAndLocalName(type, name);
                    return Flux.fromIterable(categories)
                            // key를 type:name 형식으로 맞춤
                            .map(cat -> new AbstractMap.SimpleEntry<>(type + ":" + cat.getLocalName(), cat)); // (지역명, LocalCategory)
                })

                // 2. 지역별로 외부 API 호출
                .flatMap(entry -> {
                    String regionName = entry.getKey();       // ex) "수원시"
                    LocalCategory localCategory = entry.getValue();
                    String areaCode = localCategory.getAreaCode().toString();
                    String sigunguCode = localCategory.getSigunguCode().toString();

                    return tourApiWebClient
                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/areaBasedList1")
                                    .queryParam("areaCd", areaCode)
                                    .queryParam("signguCd", sigunguCode)
                                    .queryParam("numOfRows", 150)
                                    .queryParam("baseYm", "202503")
                                    .build())
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMapMany(jsonString -> {
                                try {
                                    JsonNode root = objectMapper.readTree(jsonString);
                                    JsonNode items = root.path("response").path("body").path("items").path("item");

                                    List<String> restaurantNames = new ArrayList<>();
                                    if (items.isArray()) {
                                        for (JsonNode item : items) {
                                            if ("음식".equals(item.path("rlteCtgryLclsNm").asText())) {
                                                restaurantNames.add(item.path("rlteTatsNm").asText());
                                            }
                                        }
                                    }

                                    // ⚡ 지역명과 매칭되는 식당들 개별 방출
                                    return Flux.fromIterable(restaurantNames)
                                            .map(r -> new AbstractMap.SimpleEntry<>(regionName, r));

                                } catch (Exception e) {
                                    return Flux.error(new RuntimeException("JSON parsing error", e));
                                }
                            });
                }, 10)

                // 3. 같은 지역 이름끼리 묶기
                .collectMultimap(
                        Map.Entry::getKey,   // key = 지역명
                        Map.Entry::getValue  // value = 식당 이름
                )
                // 4. Map<String, Collection<String>> → Map<String, List<String>>
                .map(m -> m.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                e -> new ArrayList<>(e.getValue())
                        ))
                );
    }

    @Transactional(readOnly = true)
    public SlicedResponse<GetFeedCommentsDto> getFeedComments(Long feedId, Pageable pageable) {
        Feed feed = feedRepository.findByFeedId(feedId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        Slice<GetFeedCommentsDto> commentsSlice =
                commentsRepository.findCommentByFeedId(feed.getFeedId(), pageable);
        commentsSlice.getContent().forEach(c ->
                c.setProfileImgUrl(s3Service.generateGetPresignedUrl(c.getProfileImgUrl()))
        );

        return SlicedResponse.of(commentsSlice);
    }

    @Transactional
    public void writeFeedComments(WriteFeedCommentsDto dto){

        Feed feed = feedRepository.findByFeedId(dto.getFeedId())
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        Users user=usersRepository.findByNickname(dto.getNickname())
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Comments comment=new Comments(
                feed,
                user,
                dto.getContent()
        );

        commentsRepository.save(comment);

    }

    @Transactional(readOnly = true)
    public List<Map<Long,String>> searchFeed(SearchFeedDto searchFeedDto, Pageable pageable){
        //food Category 처리
        List<String> categoryStringList= searchFeedDto.getCategories();
        List<Category> categories = new ArrayList<>();
        for (String raw : categoryStringList) {
            String[] parts = raw.split(":", 2);
            if (parts.length != 2) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT); // "분류:이름" 형식 아니면 에러
            String type = parts[0].trim();  // 예: "분위기"
            String name = parts[1].trim();  // 예: "전통적인"
            if (type.isEmpty() || name.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);
            System.out.println(type);
            System.out.println(name);
            Category foodCategory = categoryRepository.findByTypeAndName(type,name);

            categories.add(foodCategory);
        }

        List<Long> categoryIds = categories.stream()
                .map(Category::getCategoryId) // Category -> categoryId(Long)
                .toList();
        //region Category 처리
        List<String> regionStringList = searchFeedDto.getRegions();
        List<LocalCategory> regions =new ArrayList<>();

        for (String raw : regionStringList) {
            String[] parts = raw.split(":", 2);
            if (parts.length != 2) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT); // "분류:이름" 형식 아니면 에러
            String type = parts[0].trim();  // 예: "경기도"
            String name = parts[1].trim();  // 예: "수원시"
            if (type.isEmpty() || name.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);

            LocalCategory region = localCategoryRepository.findFirstByTypeAndLocalNameOrderByLocalCategoryIdAsc(type, name);
            regions.add(region);
        }
        List<Long> regionIds = regions.stream()
                .map(LocalCategory::getLocalCategoryId) // Category -> categoryId(Long)
                .toList();

        // === Feed 조회 ===
        if (categoryIds.isEmpty() && regionIds.isEmpty()) {
            return List.of();
        }

        // JPA IN 절에서 []는 무조건 false → null로 치환해서 조건 무시하게 함
        List<Object[]> results = feedRepository.findFeedsByRegionAndCategoryPriority(
                categoryIds.isEmpty() ? null : categoryIds,
                regionIds.isEmpty() ? null : regionIds,
                pageable
        );

        List<Feed> feeds = results.stream()
                .map(r -> (Feed) r[0])
                .toList();

        List<Long> feedIds = feeds.stream().map(Feed::getFeedId).toList();

        // FeedPhotos 조회
        List<FeedPhotos> photos = feedPhotosRepository.findAllByFeedFeedIdInOrderByFeedFeedIdAscSequenceAsc(feedIds);
        Map<Long, List<FeedPhotos>> photosMapByFeedId = photos.stream()
                .collect(Collectors.groupingBy(fp -> fp.getFeed().getFeedId(),
                LinkedHashMap::new,
                Collectors.toList()));

        // List<Map<Long,String>> 변환
        List<Map<Long, String>> urlsMappedByFeedIds = new ArrayList<>();
        for (Feed feed : feeds) {
            List<FeedPhotos> feedPhotoList = photosMapByFeedId.getOrDefault(feed.getFeedId(), List.of());
            if (!feedPhotoList.isEmpty()) {
                FeedPhotos firstPhoto = feedPhotoList.get(0); // 맨 첫 번째 사진
                String url = s3Service.generateGetPresignedUrl(firstPhoto.getStorageKey());
                urlsMappedByFeedIds.add(Map.of(feed.getFeedId(), url)); }
        }
        System.out.println(urlsMappedByFeedIds);
        return urlsMappedByFeedIds;
    }

    @Transactional
    public void SaveFavoritePlace(Long userId, SaveFavoritePlaceDto dto) {

        // 좌표로 DB에 이미 등록된 Place가 있는지 먼저 확인
        Places place = placesRepository.findByLatitudeAndLongitude(dto.getLat(), dto.getLng());

        if (place == null) {
            // Kakao API 호출해서 Document 받아오기
            KakaoSearchResponse.Document doc = placeService.findPlaceByCategory(dto.getLat(), dto.getLng());

            if (doc == null) {
                throw new BizExceptionHandler(ErrorCode.NOT_FOUND_PLACE);
            }

            // Document → Places 매핑
            place = new Places(
                    Long.parseLong(doc.id()),
                    doc.placeName(),
                    doc.addressName(),
                    doc.roadAddressName(),
                    doc.placeUrl(),
                    dto.getLat(),     // 위도
                    dto.getLng(),     // 경도
                    doc.phone(),
                    doc.categoryGroupCode(),
                    doc.categoryGroupName()
            );

            // DB에 저장 (캐싱 목적)
            placesRepository.save(place);
        }

        // 사용자 조회
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        // 이미 찜한 장소인지 확인
        if (favoritePlaceRepository.existsByUserUserIdAndPlaceId(user.getUserId(), place.getId())) {
            throw new BizExceptionHandler(ErrorCode.EXIST_OBJECT);
        }

        // 즐겨찾기 등록
        FavoritePlace savedPlace = new FavoritePlace();
        savedPlace.setUser(user);
        savedPlace.setPlace(place);

        favoritePlaceRepository.save(savedPlace);
    }
    @Transactional
    public void SaveFavoritePlaceWithPlaceId(Long userId, Long placeId) {

        // 좌표로 DB에 이미 등록된 Place가 있는지 먼저 확인
        Places place = placesRepository.findById(placeId)
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.NOT_FOUND_PLACE));

        // 사용자 조회
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        // 이미 찜한 장소인지 확인
        if (favoritePlaceRepository.existsByUserUserIdAndPlaceId(user.getUserId(), place.getId())) {
            throw new BizExceptionHandler(ErrorCode.EXIST_OBJECT);
        }

        // 즐겨찾기 등록
        FavoritePlace savedPlace = new FavoritePlace();
        savedPlace.setUser(user);
        savedPlace.setPlace(place);

        favoritePlaceRepository.save(savedPlace);
    }



    @Transactional
    public void removePlace(Long userId, RemoveFavoritePlaceDto dto) {

        double lat = dto.getLat();
        double lng = dto.getLng();

        FavoritePlace favoritePlace =favoritePlaceRepository.findByUserUserIdAndPlaceLatitudeAndPlaceLongitude(userId,lat,lng)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.NOT_FOUND_PLACE));

        favoritePlaceRepository.delete(favoritePlace);
    }
    @Transactional
    public void removePlaceWithPlaceId(Long userId, Long placeId) {


        FavoritePlace favoritePlace =favoritePlaceRepository.findByUserUserIdAndPlaceId(userId,placeId)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.NOT_FOUND_PLACE));

        favoritePlaceRepository.delete(favoritePlace);
    }

    @Transactional
    public List<GetFavoritePlacesDto.Coordinate> getFavoritePlaces(Long userId) {

        Users user=usersRepository.findByUserId(userId)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        return favoritePlaceRepository.findAllByUserUserId(userId).stream()
                .map(fp->new GetFavoritePlacesDto.Coordinate(
                        fp.getPlace().getLatitude(),
                        fp.getPlace().getLongitude()
                ))
                .toList();
    }

    @Transactional
    public void feedLike(Long userId, Long feedId){

        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Feed feed = feedRepository.findByFeedId(feedId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        Likes like = Likes.builder()
                .feed(feed)
                .user(user)
                .build();

        likesRepository.save(like);
    }

    @Transactional
    public void feedUnLike(Long userId, Long feedId) {
        Likes like = likesRepository.findByFeedFeedIdAndUserUserId(feedId, userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.LIKE_NOT_FOUND));

        likesRepository.delete(like);
    }
}
