package com.meokplaylist.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meokplaylist.api.dto.RecommendRestaurantRequest;
import com.meokplaylist.api.dto.UrlMappedByFeedIdDto;
import com.meokplaylist.api.dto.UserPageDto;
import com.meokplaylist.api.dto.feed.FeedRegionMappingDto;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.domain.repository.category.LocalCategoryRepository;
import com.meokplaylist.domain.repository.feed.FeedPhotosRepository;
import com.meokplaylist.domain.repository.feed.FeedRepository;
import com.meokplaylist.domain.repository.socialInteraction.FollowsRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.socialInteraction.Follows;
import com.meokplaylist.infra.user.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
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

        private static final int CONTENT_TYPE_RESTAURANT = 39;

        private final FollowsRepository followsRepository;
        private final UsersRepository usersRepository;
        private final FeedRepository feedRepository;
        private final S3Service s3Service;
        @Qualifier("tourWebClient") private final WebClient tourApiWebClient;

        private final LocalCategoryRepository localCategoryRepository;
        private final FeedPhotosRepository feedPhotosRepository;
        private final ObjectMapper objectMapper = new ObjectMapper();

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

        Boolean isMe=false;

        if(user.getNickname().equals(nickName)){
            isMe=true;
            user= usersRepository.findByNickname(nickName)
                    .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));
        }

        long feedNum = feedRepository.countByUserUserId(user.getUserId());
        long followingNum =followsRepository.countByFollowingUserId(user.getUserId());
        long followerNum =followsRepository.countByFollowerUserId(user.getUserId());
        String userNickname=user.getNickname();
        String userIntro=user.getIntroduction();
        String profileUrl=s3Service.generatePutPresignedUrl(user.getProfileImgKey());

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
                .build();

        return userPageDto;
    }

    @Transactional(readOnly = true)
    public Mono<List<String>> recommendRestaurant(RecommendRestaurantRequest request){

        List<String> regions=request.getRegions();
        // 1. 요청할 지역 정보(LocalCategory)들을 Flux 스트림으로 변환

        return Flux.fromIterable(Objects.requireNonNullElse(regions, Collections.emptyList()))
                // 1. 각 지역 문자열을 파싱하고 DB에서 LocalCategory '리스트'를 조회합니다.
                .map(raw -> {
                    String[] parts = raw.split(":", 2);
                    if (parts.length != 2) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);
                    String type = parts[0].trim();
                    String name = parts[1].trim();
                    if (type.isEmpty() || name.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);

                    // 결과는 List<LocalCategory> 입니다.
                    return localCategoryRepository.findAllByTypeAndLocalName(type, name);
                })


                .flatMap(localCategoryList -> Flux.fromIterable(localCategoryList))

                .flatMap(localCategory -> {
                    String areaCode = localCategory.getAreaCode().toString();
                    String sigunguCode = localCategory.getSigunguCode().toString();

                    return tourApiWebClient
                            .get()
                            .uri(uriBuilder -> uriBuilder
                                    .path("/areaBasedList1")
                                    .queryParam("areaCd", areaCode)
                                    .queryParam("signguCd", sigunguCode)
                                    .queryParam("numOfRows", 20)
                                    .queryParam("baseYm","202503")
                                    .build())
                            .retrieve()
                            .bodyToMono(String.class);
                }, 10)


                .flatMap(jsonString -> {
                    try {
                        // 2. 받은 문자열을 JsonNode 객체로 파싱합니다.
                        JsonNode root = objectMapper.readTree(jsonString);

                        // 3. 실제 데이터가 있는 item 배열로 이동합니다.
                        JsonNode items = root.path("response").path("body").path("items").path("item");

                        List<String> restaurantNames = new ArrayList<>();
                        if (items.isArray()) {
                            for (JsonNode item : items) {
                                // 4. 카테고리가 "음식"인 경우에만 필터링합니다.
                                if ("음식".equals(item.path("rlteCtgryLclsNm").asText())) {
                                    // 5. 음식점 이름("rlteTatsNm")을 리스트에 추가합니다.
                                    restaurantNames.add(item.path("rlteTatsNm").asText());
                                }
                            }
                        }
                        // 필터링된 음식점 이름 목록을 다음 스트림으로 전달합니다.
                        return Flux.fromIterable(restaurantNames);

                    } catch (Exception e) {
                        // 파싱 오류 발생 시 에러를 전달하거나 비어있는 스트림을 반환합니다.
                        return Flux.error(new RuntimeException("JSON parsing error", e));
                    }
                })
                .distinct() // 혹시 모를 중복 제거
                .collectList(); // 6. 최종적으로 모든 음식점 이름을 하나의 리스트로 합칩니다.
    }


}
