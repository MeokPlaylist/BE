package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.category.FeedCategorySetUpRequest;
import com.meokplaylist.api.dto.feed.FeedCreateRequest;
import com.meokplaylist.api.dto.feed.FeedPhotoForm;
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
import com.meokplaylist.infra.category.UserCategory;
import com.meokplaylist.infra.feed.Feed;
import com.meokplaylist.infra.feed.FeedCategory;
import com.meokplaylist.infra.feed.FeedLocalCategory;
import com.meokplaylist.infra.feed.FeedPhotos;
import com.meokplaylist.util.StorageKeyUtil;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.ArrayList;
import java.util.Set;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {

    @Value("${cloud.ncp.object-storage.bucket}")
    private String bucketName;

    private static final Set<String> ALLOWED = Set.of("image/jpeg","image/png");

    private final S3Client objectStorageClient;
    private final FeedRepository feedRepository;
    private final FeedPhotosRepository feedPhotosRepository;
    private final FeedLocalCategoryRepository feedLocalCategoryRepository;
    private final FeedCategoryRespository feedCategoryRespository;
    private final LocalCategoryRepository localCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UsersRepository usersRepository;
    private final UserCategoryRepository userCategoryRepository;

    @Transactional
    public Boolean createFeed(FeedCreateRequest feedCreateRequest, FeedCategorySetUpRequest feedCategorySetUpRequest,Long userId) {
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Feed feed = Feed.builder()
                .user(user)
                .content(feedCreateRequest.content())
                .hashTag(feedCreateRequest.hashTag())
                .build();
        feed = feedRepository.save(feed);

        //피드 카테고리 저장
        if(feedCategorySetUpRequest.categoryNames() !=null){
            feedCategorySetUp(feedCategorySetUpRequest,feed.getFeedId());

        }

        List<FeedPhotos> feedPhotos =new ArrayList<>();

        for (FeedPhotoForm photoForm : feedCreateRequest.photos()) {

            String storageKey = putFileToBucket(photoForm.photo(), user.getUserId()); //key 리턴

            FeedPhotos photo = FeedPhotos.builder()
                    .feed(feed)
                    .latitude(photoForm.latitude())
                    .longitude(photoForm.longitude())
                    .dayAndTime(photoForm.dayAndTime())
                    .sequence(photoForm.sequence())
                    .storageKey(storageKey)
                    .build();
            feedPhotos.add(photo);

        }

        feedPhotosRepository.saveAll(feedPhotos);
        return true;
    }

    @Transactional
    public void feedCategorySetUp(FeedCategorySetUpRequest request, Long feedId) {

        // 1. 피드 조회
        Feed feed = feedRepository.findByFeedId(feedId)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.NOT_FOUND_FEED));

        // 2. 카테고리 이름 목록 가져오기
        List<String> categoryNames = request.categoryNames();  // ex) ["분위기:로맨틱", "음식:한식"]
        List<String> categoryLocalNames = request.categoryLocalNames();

        // 3. 이름으로 카테고리 엔티티 조회
        List<Category> foodCategories = categoryRepository.findAllByNameIn(categoryNames);

        if (foodCategories.isEmpty()){
            throw new BizExceptionHandler(ErrorCode.CATEGORY_NOT_FOUND);
        }

        // 4. 매핑 저장
        for (Category category : foodCategories) {

            FeedCategory feedCategory = new FeedCategory(category,feed);
            feedCategoryRespository.save(feedCategory);
        }

        if(!categoryLocalNames.isEmpty()){

            List<LocalCategory> localCategories= localCategoryRepository.findAllByLocalNameIn(categoryLocalNames);

            for (LocalCategory localCategory : localCategories) {


                FeedLocalCategory feedLocalCategory = new FeedLocalCategory(localCategory,feed);

                feedLocalCategoryRepository.save(feedLocalCategory);

            }

        }


    }

    @Transactional
    private String putFileToBucket(MultipartFile file,Long userId) {

        if (file == null || file.isEmpty()) {
            throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);
        }
        String ct = Optional.ofNullable(file.getContentType()).orElse("");

        if (!ALLOWED.contains(ct)) {
            throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);
        }
        System.out.println("ct= "+ ct);
        try {
            String key = StorageKeyUtil.buildKey("photos", userId, file.getOriginalFilename());
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            objectStorageClient.putObject(putReq, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return key;

        } catch (IOException e) {
            throw new BizExceptionHandler(ErrorCode.FAILED_TO_UPLOAD_FILE);
        }
    }

    @Transactional
    public void mainFeedSelect(Long userId){
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        List<UserCategory> userCategory=userCategoryRepository.findByUserUserId(user.getUserId())
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.NOT_FOUND_USERCATEGORY));

        for(int i=0; i< userCategory.size(); i++){
            Category category = userCategory.get(i).getCategory();
        }
        //개발 대기

    }



}

