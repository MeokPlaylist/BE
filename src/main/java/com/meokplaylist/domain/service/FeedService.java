package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.feed.FeedCreateRequest;
import com.meokplaylist.api.dto.feed.FeedPhotoForm;
import com.meokplaylist.api.dto.feed.FeedResponse;
import com.meokplaylist.api.dto.feed.SlicedResponse;
import com.meokplaylist.api.dto.mainFeedResponse;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.domain.repository.category.CategoryRepository;
import com.meokplaylist.domain.repository.category.LocalCategoryRepository;
import com.meokplaylist.domain.repository.category.UserCategoryRepository;
import com.meokplaylist.domain.repository.feed.FeedCategoryRespository;
import com.meokplaylist.domain.repository.feed.FeedLocalCategoryRepository;
import com.meokplaylist.domain.repository.feed.FeedPhotosRepository;
import com.meokplaylist.domain.repository.feed.FeedRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;


import java.util.*;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    @Value("${cloud.ncp.object-storage.bucket}")
    private String bucketName;

    private static final Set<String> ALLOWED = Set.of("image/jpeg","image/png");

    private final FeedRepository feedRepository;
    private final FeedPhotosRepository feedPhotosRepository;
    private final FeedLocalCategoryRepository feedLocalCategoryRepository;
    private final FeedCategoryRespository feedCategoryRespository;
    private final LocalCategoryRepository localCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UsersRepository usersRepository;
    private final UserCategoryRepository userCategoryRepository;

    private final S3Service s3Service;

    @Transactional
    public List<String> createFeed(FeedCreateRequest feedCreateRequest,Long userId) {
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Feed feed = Feed.builder()
                .user(user)
                .content(feedCreateRequest.getContent())
                .hashTag(feedCreateRequest.getHashTag())
                .build();
        feed = feedRepository.save(feed);
        //피드 카테고리 저장
        if(feedCreateRequest.getCategories() !=null){
            List<String> categories=feedCreateRequest.getCategories();
            List<String> regions =feedCreateRequest.getRegions();
            feedCategorySetUp(categories,regions,feed.getFeedId());
        }


        List<FeedPhotos> feedPhotos =new ArrayList<>();
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
        List<String> presignedUrlList =new ArrayList<>();

        feedPhotosRepository.saveAll(feedPhotos);

        for (int i=0; i <feedPhotos.size(); i++){
            String fileKey=feedPhotos.get(i).getStorageKey();
            String presignedUrl = s3Service.generatePutPresignedUrl(fileKey);
            presignedUrlList.add(presignedUrl);
        }

        System.out.println(presignedUrlList);

        return presignedUrlList;
    }


    @Transactional
    public void feedCategorySetUp(List<String> categories,List<String> regions , Long feedId) {
        // 1. 유저 조회
        Feed feed = feedRepository.findByFeedId(feedId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));
 // ["분위기:전통적인", "음식:한식", ...]
        //이거 왜 처리함
        //if (categories == null || categories.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);

        List<Category> saveCategories =new ArrayList<>();

        for (String raw : categories) {
            String[] parts = raw.split(":", 2);
            if (parts.length != 2) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT); // "분류:이름" 형식 아니면 에러
            String type = parts[0].trim();  // 예: "분위기"
            String name = parts[1].trim();  // 예: "전통적인"
            if (type.isEmpty() || name.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);

            Category foodCategory = categoryRepository.findByTypeAndName(type,name);


            saveCategories.add(foodCategory);
        }

        List<FeedCategory> mappings = saveCategories.stream()
                .map(cat -> new FeedCategory(cat, feed))   // user는 앞에서 조회된 Users
                .toList();

        feedCategoryRespository.saveAll(mappings);

        List<LocalCategory> saveRegion =new ArrayList<>();


        for (String raw : regions) {
            String[] parts = raw.split(":", 2);
            if (parts.length != 2) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT); // "분류:이름" 형식 아니면 에러
            String type = parts[0].trim();  // 예: "경기도"
            String name = parts[1].trim();  // 예: "수원시"
            if (type.isEmpty() || name.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);

            LocalCategory region = localCategoryRepository.findByTypeAndLocalName(type, name);


            saveRegion.add(region);
        }

        List<FeedLocalCategory> mapping = saveRegion.stream()
                .map(reg->new FeedLocalCategory(reg,feed))
                .toList();

        feedLocalCategoryRepository.saveAll(mapping);

    }

    @Transactional(readOnly = true)
    public Page<mainFeedResponse> mainFeedSelect(Long userId, Pageable pageable){
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Page<Feed> page;

        page =feedRepository.findFollowingFeeds(user.getUserId(),pageable); //새로 업로드한 피드가 여러개일수 있다.

        if(page.isEmpty()||page==null){ //최근 업로드 된 피드가 없다면 카테고리를 이용하여 피드 갱신

            List<Long> categoryIds = userCategoryRepository.findCategoryIdsByUserId(user.getUserId());

            if (categoryIds.isEmpty()) {
                throw new BizExceptionHandler(ErrorCode.NOT_FOUND_USERCATEGORY);
            }
            page =feedRepository.findCategoryIds(categoryIds, pageable);

        }

        List<Long> feedIdList = page.getContent().stream()
                .map(Feed::getFeedId) // 각 Feed 객체에서 ID를 추출
                .toList();
        List<FeedPhotos> feedPhotos=feedPhotosRepository.findAllByFeedFeedIdIn(feedIdList);

        Map<Long, List<FeedPhotos>> photosMapByFeedId = feedPhotos.stream()
                .collect(Collectors.groupingBy(photo -> photo.getFeed().getFeedId()));

        // 4. 최종 결과를 담을 Map<Long, List<String>>을 생성합니다. (Key: feedId, Value: URL 리스트)
        Map<Long, List<String>> feedUrlsMap = new HashMap<>();

        // 5. 그룹화된 photosMapByFeedId를 순회하며 Presigned URL을 생성합니다.
        for (Map.Entry<Long, List<FeedPhotos>> entry : photosMapByFeedId.entrySet()) {
            Long feedId = entry.getKey();
            List<FeedPhotos> photosOfFeed = entry.getValue();

            // 해당 feedId에 속한 사진들의 URL만 담을 리스트를 생성
            List<String> urls = new ArrayList<>();
            for (FeedPhotos photo : photosOfFeed) {
                urls.add(s3Service.generateGetPresignedUrl(photo.getStorageKey()));
            }
            feedUrlsMap.put(feedId, urls);
        }

        Page<mainFeedResponse> responsePage = page.map(feed -> {

            // getOrDefault를 사용하면 해당 feedId에 사진이 없을 경우, Null 대신 빈 리스트를 반환
            List<String> urlsForThisFeed = feedUrlsMap.getOrDefault(feed.getFeedId(), Collections.emptyList());

            return new mainFeedResponse(
                    feed.getUser().getNickname(),
                    feed.getContent(),
                    feed.getHashTag(),
                    feed.getCreatedAt(),
                    urlsForThisFeed
            );

        });

        return responsePage;
    }
    @Transactional(readOnly = true)
    public SlicedResponse<FeedResponse> mainFeedSelectSlice(Long userId, Pageable pageable) {
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        // Page로 받아도 무방 (Slice 전환은 리포지토리 시그니처를 Slice로 바꾸면 더 좋음)
        Page<Feed> page = feedRepository.findFollowingFeeds(user.getUserId(), pageable);

        if (page.isEmpty()) {
            List<Long> categoryIds = userCategoryRepository.findCategoryIdsByUserId(user.getUserId());
            if (categoryIds.isEmpty()) {
                // 빈 결과를 slice 형태로 반환
                return new SlicedResponse<>(List.of(),
                        pageable.getPageNumber(), pageable.getPageSize(),
                        true, true, true, false);
            }
            page = feedRepository.findCategoryIds(categoryIds, pageable);
        }

        // 이번 페이지의 feedId 수집
        List<Feed> feeds = page.getContent();
        List<Long> feedIds = feeds.stream().map(Feed::getFeedId).toList();

        // 사진 일괄 조회(정렬 보장)
        final Map<Long, List<String>> feedUrlsMap;

        if (!feedIds.isEmpty()) {
            List<FeedPhotos> photos = feedPhotosRepository.findAllByFeedFeedIdIn(feedIds);
            Map<Long, List<FeedPhotos>> photosMapByFeedId = photos.stream()
                    .collect(Collectors.groupingBy(fp -> fp.getFeed().getFeedId(), LinkedHashMap::new, Collectors.toList()));

            Map<Long, List<String>> tmp = new HashMap<>(photosMapByFeedId.size());
            for (var e : photosMapByFeedId.entrySet()) {
                List<String> urls = new ArrayList<>(e.getValue().size());
                for (FeedPhotos p : e.getValue()) {
                    urls.add(s3Service.generateGetPresignedUrl(p.getStorageKey()));
                }
                tmp.put(e.getKey(), urls);
            }
            feedUrlsMap = tmp;
        } else {
            feedUrlsMap = Collections.emptyMap();
        }

        // 엔티티 -> DTO
        List<FeedResponse> content = feeds.stream().map(feed -> new FeedResponse(
                feed.getUser().getNickname(),
                feed.getContent(),
                feed.getHashTag(),
                feed.getCreatedAt(),
                feedUrlsMap.getOrDefault(feed.getFeedId(), List.of())
        )).toList();

        // Page를 Slice처럼 포장 (hasNext는 Page에서 가져와도 동일)
        Slice<FeedResponse> sliceView = new org.springframework.data.domain.SliceImpl<>(
                content, pageable, page.hasNext()
        );

        return SlicedResponse.of(sliceView);
    }
}

