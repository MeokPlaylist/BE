package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.SlicedResponse;
import com.meokplaylist.api.dto.feed.*;
import com.meokplaylist.api.dto.presignedUrl.PresignedPutListUrlAndFeedIdResponse;
import com.meokplaylist.api.dto.socialInteraction.CheckUserLikeFeedDto;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.domain.repository.category.CategoryRepository;
import com.meokplaylist.domain.repository.category.LocalCategoryRepository;
import com.meokplaylist.domain.repository.category.UserCategoryRepository;
import com.meokplaylist.domain.repository.feed.FeedCategoryRespository;
import com.meokplaylist.domain.repository.feed.FeedLocalCategoryRepository;
import com.meokplaylist.domain.repository.feed.FeedPhotosRepository;
import com.meokplaylist.domain.repository.feed.FeedRepository;
import com.meokplaylist.domain.repository.socialInteraction.CommentsRepository;
import com.meokplaylist.domain.repository.socialInteraction.LikesRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.user.Users;
import com.meokplaylist.infra.category.Category;
import com.meokplaylist.infra.category.LocalCategory;
import com.meokplaylist.infra.feed.Feed;
import com.meokplaylist.infra.feed.FeedCategory;
import com.meokplaylist.infra.feed.FeedLocalCategory;
import com.meokplaylist.infra.feed.FeedPhotos;
import com.meokplaylist.util.StorageKeyUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    @Value("${cloud.ncp.object-storage.bucket}")
    private String bucketName;

    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png");

    private final FeedRepository feedRepository;
    private final FeedPhotosRepository feedPhotosRepository;
    private final FeedLocalCategoryRepository feedLocalCategoryRepository;
    private final FeedCategoryRespository feedCategoryRepository;
    private final LocalCategoryRepository localCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UsersRepository usersRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final LikesRepository likesRepository;
    private final CommentsRepository commentsRepository;

    private final S3Service s3Service;

    @Transactional
    public PresignedPutListUrlAndFeedIdResponse createFeed(FeedCreateRequest feedCreateRequest, Long userId) {
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Feed feed = Feed.builder()
                .user(user)
                .content(feedCreateRequest.getContent())
                .hashTag(feedCreateRequest.getHashTag())
                .build();
        feed = feedRepository.save(feed);
        //피드 카테고리 저장
        if (feedCreateRequest.getCategories() != null) {
            List<String> categories = feedCreateRequest.getCategories();
            List<String> regions = feedCreateRequest.getRegions();
            feedCategorySetUp(categories, regions, feed.getFeedId());
        }


        List<FeedPhotos> feedPhotos = new ArrayList<>();
        for (FeedPhotoForm photoForm : feedCreateRequest.getPhotos()) {

            String storageKey = StorageKeyUtil.buildKey("feeds", userId, feed.getFeedId(), photoForm.getFileName());//key 리턴

            FeedPhotos photo = FeedPhotos.builder()
                    .feed(feed)
                    .latitude(photoForm.getLatitude())
                    .longitude(photoForm.getLongitude())
                    .dayAndTime(photoForm.getDayAndTime())
                    .sequence(photoForm.getSequence())
                    .storageKey(storageKey)
                    .build();
            feedPhotos.add(photo);

        }
        List<String> presignedUrlList = new ArrayList<>();

        feedPhotosRepository.saveAll(feedPhotos);

        for (int i = 0; i < feedPhotos.size(); i++) {
            String fileKey = feedPhotos.get(i).getStorageKey();
            String presignedUrl = s3Service.generatePutPresignedUrl(fileKey);
            presignedUrlList.add(presignedUrl);
        }
        return new PresignedPutListUrlAndFeedIdResponse(presignedUrlList,feed.getFeedId());
    }


    @Transactional
    public void feedCategorySetUp(List<String> categories, List<String> regions, Long feedId) {
        // 1. 유저 조회??
        Feed feed = feedRepository.findByFeedId(feedId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));


        List<Category> saveCategories = new ArrayList<>();

        for (String raw : categories) {
            String[] parts = raw.split(":", 2);
            if (parts.length != 2) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT); // "분류:이름" 형식 아니면 에러
            String type = parts[0].trim();  // 예: "분위기"
            String name = parts[1].trim();  // 예: "전통적인"
            if (type.isEmpty() || name.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);

            Category foodCategory = categoryRepository.findByTypeAndName(type, name);

            saveCategories.add(foodCategory);
        }

        List<FeedCategory> mappings = saveCategories.stream()
                .map(cat -> new FeedCategory(cat, feed))   // user는 앞에서 조회된 Users ??
                .toList();

        feedCategoryRepository.saveAll(mappings);

        List<LocalCategory> saveRegion = new ArrayList<>();


        for (String raw : regions) {
            String[] parts = raw.split(":", 2);
            if (parts.length != 2) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT); // "분류:이름" 형식 아니면 에러
            String type = parts[0].trim();  // 예: "경기도"
            String name = parts[1].trim();  // 예: "수원시"
            if (type.isEmpty() || name.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);

            LocalCategory region = localCategoryRepository.findFirstByTypeAndLocalNameOrderByLocalCategoryIdAsc(type, name);


            saveRegion.add(region);
        }

        List<FeedLocalCategory> mapping = saveRegion.stream()
                .map(reg -> new FeedLocalCategory(reg, feed))
                .toList();

        feedLocalCategoryRepository.saveAll(mapping);

    }

    @Transactional(readOnly = true)
    public SlicedResponse<MainFeedResponse> mainFeedSelectSlice(Long userId, Pageable pageable) {
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Slice<Feed> slice = feedRepository.findFollowingFeeds(user.getUserId(), pageable);

        if (slice.getContent().isEmpty()) {
            List<Long> categoryIds = userCategoryRepository.findCategoryIdsByUserId(user.getUserId());
            if (categoryIds.isEmpty()) {
                Slice<MainFeedResponse> emptySlice = new SliceImpl<>(List.of(), pageable, false);
                return SlicedResponse.of(emptySlice);
            }
            slice = feedRepository.findCategoryIds(categoryIds, pageable);
        }

        List<Feed> feeds = slice.getContent();
        if (feeds.isEmpty()) {
            return SlicedResponse.of(new SliceImpl<>(List.of(), pageable, false));
        }

        List<Long> feedIds = feeds.stream().map(Feed::getFeedId).toList();

        // 사진, 좋아요, 댓글, 유저 좋아요 여부 조회
        final Map<Long, FeedMapDto> feedUrlsAndSocialMap;
        Map<Long, Boolean> likeBooleanMapByFeedId;

        if (!feedIds.isEmpty()) {
            List<FeedPhotos> photos = feedPhotosRepository.findAllByFeedFeedIdInOrderByFeedFeedIdAscSequenceAsc(feedIds);
            List<LikeCountDto> likeCounts = likesRepository.countLikesByFeedIds(feedIds);
            List<CommentCountDto> commentCounts = commentsRepository.findCommentCountsByFeedIds(feedIds);
            List<CheckUserLikeFeedDto> feedLike = likesRepository.findByFeedIdsAndUserId(feedIds, userId);

            likeBooleanMapByFeedId = feedLike.stream()
                    .collect(Collectors.toMap(
                            CheckUserLikeFeedDto::getFeedId,
                            CheckUserLikeFeedDto::getIslike
                    ));

            Map<Long, Long> likeMapByFeedId = likeCounts.stream()
                    .collect(Collectors.toMap(
                            LikeCountDto::getFeedId,
                            LikeCountDto::getLikeCount
                    ));

            Map<Long, Long> commentMapByFeedId = commentCounts.stream()
                    .collect(Collectors.toMap(
                            CommentCountDto::getFeedId,
                            CommentCountDto::getCommentCount
                    ));

            Map<Long, List<FeedPhotos>> photosMapByFeedId = photos.stream()
                    .collect(Collectors.groupingBy(
                            fp -> fp.getFeed().getFeedId(),
                            LinkedHashMap::new,
                            Collectors.toList()
                    ));

            Map<Long, FeedMapDto> tmp = new HashMap<>(photosMapByFeedId.size());
            for (var e : photosMapByFeedId.entrySet()) {
                List<String> urls = new ArrayList<>(e.getValue().size());
                for (FeedPhotos p : e.getValue()) {
                    urls.add(s3Service.generateGetPresignedUrl(p.getStorageKey()));
                }

                long likeCount = likeMapByFeedId.getOrDefault(e.getKey(), 0L);
                long commentCount = commentMapByFeedId.getOrDefault(e.getKey(), 0L);

                FeedMapDto feedMapDto = new FeedMapDto(urls, likeCount, commentCount);
                tmp.put(e.getKey(), feedMapDto);
            }
            feedUrlsAndSocialMap = tmp;
        } else {
            feedUrlsAndSocialMap = Collections.emptyMap();
        }

        //  각 Feed의 작성자 프로필 URL 포함
        List<MainFeedResponse> feedSliceDto = feeds.stream().map(feed -> {
            String profileUrl = null;
            if (feed.getUser() != null && feed.getUser().getProfileImgKey() != null) {
                profileUrl = s3Service.generateGetPresignedUrl(feed.getUser().getProfileImgKey());
            }

            FeedMapDto mapDto = feedUrlsAndSocialMap.get(feed.getFeedId());
            List<String> photoUrls = mapDto != null ? mapDto.getPhotoUrls() : List.of();

            boolean isLiked = likesRepository.existsByFeedFeedIdAndUserUserId(feed.getFeedId(), userId);
            long likeCount = mapDto != null ? mapDto.getLikeCoount() : 0L;
            long commentCount = mapDto != null ? mapDto.getCommetCount() : 0L;

            return new MainFeedResponse(
                    feed.getUser().getNickname(),
                    profileUrl,
                    feed.getFeedId(),
                    feed.getContent(),
                    feed.getHashTag(),
                    feed.getCreatedAt(),
                    photoUrls,
                    isLiked,
                    likeCount,
                    commentCount
            );
        }).toList();

        Slice<MainFeedResponse> sliceView = new SliceImpl<>(feedSliceDto, pageable, slice.hasNext());
        return SlicedResponse.of(sliceView);
    }

    @Transactional
    public void deleteFeed(Long feedId,Long userId){
        Feed feed = feedRepository.findByFeedId(feedId)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        if(!feed.getUser().getUserId().equals(userId)){
            throw new BizExceptionHandler(ErrorCode.DONT_HAVE_AUTHORITY);
        }

        String prefix="feeds"+"/"+feed.getUser().getUserId()+"/"+feed.getFeedId()+"/";
        s3Service.deleteByPrefix(prefix);

        feedRepository.delete(feed);

    }

    @Transactional
    public void modifyFeedCategory(ModifyFeedCategoryDto dto,Long userId){
        Feed feed = feedRepository.findByFeedId(dto.getFeedId())
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        if(!feed.getUser().getUserId().equals(userId)){
            throw new BizExceptionHandler(ErrorCode.DONT_HAVE_AUTHORITY);
        }
        List<Long> feedCategoryIds = feed.getFeedCategories().stream()
                .map(FeedCategory::getId)  // FeedCategory 엔티티의 PK getter
                .toList();
        if (!feedCategoryIds.isEmpty()) {
            feedCategoryRepository.deleteAllById(feedCategoryIds);
        }

        // --- FeedLocalCategory 삭제 (ID 기반) ---
        List<Long> feedLocalCategoryIds = feed.getFeedLocalCategories().stream()
                .map(FeedLocalCategory::getId)
                .toList();
        if (!feedLocalCategoryIds.isEmpty()) {
            feedLocalCategoryRepository.deleteAllById(feedLocalCategoryIds);
        }
        if (dto.getCategories() != null) {
            List<String> categories = dto.getCategories();
            List<String> regions = dto.getRegions();
            feedCategorySetUp(categories, regions, feed.getFeedId());
        }
        System.out.println(feed.getFeedCategories());
        System.out.println(feed.getFeedLocalCategories());
    }

    @Transactional
    public void modifyFeedContent(ModifyFeedContentDto dto,Long userId){

        Feed feed = feedRepository.findByFeedId(dto.getFeedId())
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        if(!feed.getUser().getUserId().equals(userId)){
            throw new BizExceptionHandler(ErrorCode.DONT_HAVE_AUTHORITY);
        }
        //피드 content삭제후 저장해야함 안드러면 중복 저장
        feed.setContent(dto.getContent());
        feedRepository.save(feed);

    }

    @Transactional
    public Boolean modifyMainFeedPhoto(ModifyMainFeedPhotoDto dto,Long userId){

        Feed feed = feedRepository.findByFeedId(dto.getFeedId())
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        if(!feed.getUser().getUserId().equals(userId)){
            throw new BizExceptionHandler(ErrorCode.ERROR_CODE);
        }


        int oldSequence= dto.getOldMainFeedPhotoSequence();
        int newSequence =dto.getNewMainFeedPhotoSequence();

        if(oldSequence>20||newSequence>20){
            throw new BizExceptionHandler(ErrorCode.SEQUENCE_SET_MISMATCH);
        }

        FeedPhotos oldFeedPhotos=feedPhotosRepository.findByFeedFeedIdAndSequence(feed.getFeedId(),oldSequence)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.NOT_FOUND_FEEDPHOTO));

        FeedPhotos newFeedPhotos=feedPhotosRepository.findByFeedFeedIdAndSequence(feed.getFeedId(),newSequence)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.NOT_FOUND_FEEDPHOTO));

        oldFeedPhotos.setSequence(newSequence);

        newFeedPhotos.setSequence(oldSequence);

        return true;
    }

    @Transactional
    public GetDetailInforDto getDetailInfo(Long userId, Long feedId){
        usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Feed feed = feedRepository.findByFeedId(feedId)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        Users feedUser=feed.getUser();
        List<String> feedPhotoStorageKey = feed.getFeedPhotos().stream()
                .map(FeedPhotos::getStorageKey) // FeedPhoto 객체에서 storageKey만 추출
                .toList();

        List<String> feedPhotoUrls=new ArrayList<>(feedPhotoStorageKey.size());

        for(String storagekey:feedPhotoStorageKey){
            String url=s3Service.generateGetPresignedUrl(storagekey);
            feedPhotoUrls.add(url);
        }
        // 4. 카테고리 조회
        List<String> categoryNames = feedCategoryRepository.findByFeedFeedId(feedId).stream()
                .map(fc -> fc.getCategory().getName())
                .toList();

        // 5. 지역 카테고리 조회
        List<String> localCategoryNames = feedLocalCategoryRepository.findByFeedFeedId(feedId).stream()
                .map(fl -> fl.getLocalCategory().getType() + ":" + fl.getLocalCategory().getLocalName())
                .toList();

        // 6. 하나의 리스트로 합치기
        List<String> mergedCategories = new ArrayList<>();
        mergedCategories.addAll(categoryNames);
        mergedCategories.addAll(localCategoryNames);

        Long likeNum=likesRepository.countLikesByFeedFeedId(feed.getFeedId());
        Long commentNum=commentsRepository.countCommentByFeedFeedId(feed.getFeedId());
        Boolean feedLike = likesRepository.existsByFeedFeedIdAndUserUserId(feedId, userId);
        String profileUrl = s3Service.generateGetPresignedUrl(feedUser.getProfileImgKey());
        GetDetailInforDto response = new GetDetailInforDto(
                feedUser.getNickname(),
                feed.getContent(),
                profileUrl,
                feed.getHashTag(),
                feed.getCreatedAt(),
                feedPhotoUrls,
                mergedCategories,
                feedLike,
                likeNum,
                commentNum
        );

        return response;
    }
}

