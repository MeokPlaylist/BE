package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.category.FeedCategorySetUpRequest;
import com.meokplaylist.api.dto.feed.FeedCreateRequest;
import com.meokplaylist.api.dto.feed.FeedPhotoRequest;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.domain.repository.category.CategoryRepository;
import com.meokplaylist.domain.repository.category.LocalCategoryRepository;
import com.meokplaylist.domain.repository.feed.FeedCategoryRespository;
import com.meokplaylist.domain.repository.feed.FeedLocalCategoryRepository;
import com.meokplaylist.domain.repository.feed.FeedPhotosRepository;
import com.meokplaylist.domain.repository.feed.FeedRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.Users;
import com.meokplaylist.infra.category.Category;
import com.meokplaylist.infra.category.LocalCategory;
import com.meokplaylist.infra.category.UserCategory;
import com.meokplaylist.infra.category.UserLocalCategory;
import com.meokplaylist.infra.feed.Feed;
import com.meokplaylist.infra.feed.FeedCategory;
import com.meokplaylist.infra.feed.FeedLocalCategory;
import com.meokplaylist.infra.feed.FeedPhotos;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final FeedPhotosRepository feedPhotosRepository;
    private final FeedLocalCategoryRepository feedLocalCategoryRepository;
    private final FeedCategoryRespository feedCategoryRespository;
    private final LocalCategoryRepository localCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final UsersRepository usersRepository;

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


        /*

        + 사진 storage 저장한는 서비스 + photo storageUrl 저장하는과정 필요

        */


        for (FeedPhotoRequest photoRequest : feedCreateRequest.photos()) {

            FeedPhotos photo = FeedPhotos.builder()
                    .feed(feed)
                    .latitude(photoRequest.latitude())
                    .longitude(photoRequest.longitude())
                    .dayAndTime(photoRequest.dayAndTime())
                    .sequence(photoRequest.sequence())
                    .build();

            feedPhotosRepository.save(photo);
        }

        return true;
    }


    @Transactional
    public void feedCategorySetUp(FeedCategorySetUpRequest request, Long feedId) {
        // 1. 유저 조회
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
}

