package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.feed.*;
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
import lombok.extern.slf4j.Slf4j;


import java.util.*;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    @Value("${cloud.ncp.object-storage.bucket}")
    private String bucketName;

    private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png");

    private final FeedRepository feedRepository;
    private final FeedPhotosRepository feedPhotosRepository;
    private final FeedLocalCategoryRepository feedLocalCategoryRepository;
    private final FeedCategoryRespository feedCategoryRespository;
    private final LocalCategoryRepository localCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UsersRepository usersRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final LikesRepository likesRepository;
    private final CommentsRepository commentsRepository;

    private final S3Service s3Service;

    @Transactional
    public List<String> createFeed(FeedCreateRequest feedCreateRequest, Long userId) {
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
        return presignedUrlList;
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

        feedCategoryRespository.saveAll(mappings);

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
                // 빈 결과를 slice 형태로 반환
                Slice<MainFeedResponse> emptySlice = new SliceImpl<>(List.of(), pageable, false);
                return SlicedResponse.of(emptySlice);
            }
            slice = feedRepository.findCategoryIds(categoryIds, pageable);
        }

        // 이번 페이지의 feedId 수집
        List<Feed> feeds = slice.getContent();

        if (feeds.isEmpty()) { //방어 코드
            return SlicedResponse.of(new SliceImpl<>(List.of(), pageable, false));
        }

        List<Long> feedIds = feeds.stream().map(Feed::getFeedId).toList();

        // 사진 일괄 조회(정렬 보장)
        final Map<Long, FeedMapDto> feedUrlsAndSocialMap;

        if (!feedIds.isEmpty()) {
            List<FeedPhotos> photos = feedPhotosRepository.findAllByFeedFeedIdInOrderByFeedFeedIdAscSequenceAsc(feedIds);
            List<LikeCountDto> likeCounts = likesRepository.countLikesByFeedIds(feedIds);

            List<CommentCountDto> commentCounts = commentsRepository.findCommentCountsByFeedIds(feedIds);

            Map<Long, Long> likeMapByFeedId = likeCounts.stream()
                    .collect(Collectors.toMap(
                            LikeCountDto::getFeedId,
                            LikeCountDto::getLikeCount
                    ));

            Map<Long, Long> commnetMapByFeedId = commentCounts.stream()
                    .collect(Collectors.toMap(
                            CommentCountDto::getFeedId,
                            CommentCountDto::getCommentCount
                    ));


            Map<Long, List<FeedPhotos>> photosMapByFeedId = photos.stream()
                    .collect(Collectors.groupingBy(fp -> fp.getFeed().getFeedId(), LinkedHashMap::new, Collectors.toList()));

            Map<Long, FeedMapDto> tmp = new HashMap<>(photosMapByFeedId.size());
            for (var e : photosMapByFeedId.entrySet()) {
                List<String> urls = new ArrayList<>(e.getValue().size());
                for (FeedPhotos p : e.getValue()) {
                    urls.add(s3Service.generateGetPresignedUrl(p.getStorageKey()));
                }
                long likeCount = likeMapByFeedId.getOrDefault(e.getKey(), 0L);
                long commnetCount = commnetMapByFeedId.getOrDefault(e.getKey(), 0L);


                FeedMapDto feedMapDto = new FeedMapDto(urls, likeCount, commnetCount);

                tmp.put(e.getKey(), feedMapDto);
            }

            feedUrlsAndSocialMap = tmp;
        } else {
            feedUrlsAndSocialMap = Collections.emptyMap();
        }

        // 엔티티 -> DTO
        List<MainFeedResponse> feedSliceDto = feeds.stream().map(feed -> new MainFeedResponse(
                feed.getUser().getNickname(),
                feed.getFeedId(),
                feed.getContent(),
                feed.getHashTag(),
                feed.getCreatedAt(),
                feedUrlsAndSocialMap.get(feed.getFeedId()).getPhotoUrls(),
                feedUrlsAndSocialMap.get(feed.getFeedId()).getLikeCoount(),
                feedUrlsAndSocialMap.get(feed.getFeedId()).getCommetCount()
        )).toList();

        Slice<MainFeedResponse> sliceView = new SliceImpl<>(feedSliceDto, pageable, slice.hasNext());

        return SlicedResponse.of(sliceView);
    }

}

