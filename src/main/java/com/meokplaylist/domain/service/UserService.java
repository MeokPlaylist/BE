package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.Boolean.BooleanRequest;
import com.meokplaylist.api.dto.FeedPhotosWithLocalDto;
import com.meokplaylist.api.dto.FeedPhotosWithYearDto;
import com.meokplaylist.api.dto.GetFollowResponse;
import com.meokplaylist.api.dto.UrlMappedByFeedIdDto;
import com.meokplaylist.api.dto.presignedUrl.PresignedGetUrlResponse;
import com.meokplaylist.api.dto.category.CategorySetUpRequest;
import com.meokplaylist.api.dto.user.*;
import com.meokplaylist.domain.repository.UserConsentRepository;
import com.meokplaylist.domain.repository.UserOauthRepository;
import com.meokplaylist.domain.repository.UsersRepository;

import com.meokplaylist.domain.repository.category.CategoryRepository;
import com.meokplaylist.domain.repository.category.LocalCategoryRepository;
import com.meokplaylist.domain.repository.category.UserCategoryRepository;
import com.meokplaylist.domain.repository.category.UserLocalCategoryRepository;
import com.meokplaylist.domain.repository.feed.FeedPhotosRepository;
import com.meokplaylist.domain.repository.feed.FeedRepository;
import com.meokplaylist.domain.repository.socialInteraction.FollowsRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.category.Category;
import com.meokplaylist.infra.category.LocalCategory;
import com.meokplaylist.infra.category.UserCategory;
import com.meokplaylist.infra.category.UserLocalCategory;
import com.meokplaylist.infra.user.UserConsent;
import com.meokplaylist.infra.user.UserOauth;
import com.meokplaylist.infra.user.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {


    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConsentRepository userConsentRepository;
    private  final CategoryRepository categoryRepository;
    private final LocalCategoryRepository localCategoryRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserOauthRepository userOauthRepository;
    private final FeedRepository feedRepository;
    private final FollowsRepository followsRepository;
    private final FeedPhotosRepository feedPhotosRepository;
    private final UserLocalCategoryRepository userLocalCategoryRepository;

    private final S3Service s3Service;
    private static String consentFileUrl ="https://kr.object.ncloudstorage.com/meokplaylist/%EB%A8%B9%ED%94%8C%EB%A6%AC%20%EB%8F%99%EC%9D%98%EC%84%9C%20%EB%82%B4%EC%9A%A9.txt";

    @Transactional(readOnly = true)
    public Long findUser(FindUserRequest request){
        Users user = usersRepository.findByEmailAndPasswordHashIsNotNull(request.email())
                        .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));


        return user.getUserId();
    }

    //패스워드 새로 설정
    @Transactional
    public Boolean newPassword(NewPasswordRequest request){
        Users user =usersRepository.findByUserId(request.userId())
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        return true;
    }

    @Transactional
    public Boolean consentUpload(BooleanRequest request, Long userId){

        if (request.isAvailable() == null) {
            throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);
        }
        Users user =usersRepository.findByUserId(userId)
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));
        if(request.isAvailable()) {
            UserConsent userConsent = new UserConsent(user, "v1.0", consentFileUrl);
            userConsentRepository.save(userConsent);
            return true;
        }
        else{
            throw new BizExceptionHandler(ErrorCode.ERROR_CODE);
        }

    }

    //유저 카테고리 설정
    @Transactional
    public Boolean categorySetUp(CategorySetUpRequest request, Long userId) {
        // 1. 유저 조회
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        List<String> categoryList = request.categories(); // ["분위기:전통적인", "음식:한식", ...]
        if (categoryList == null || categoryList.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);

        List<Category> saveCategories =new ArrayList<>();

        for (String raw : categoryList) {
            String[] parts = raw.split(":", 2);
            if (parts.length != 2) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT); // "분류:이름" 형식 아니면 에러
            String type = parts[0].trim();  // 예: "분위기"
            String name = parts[1].trim();  // 예: "전통적인"
            if (type.isEmpty() || name.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);

            Category foodCategory = categoryRepository.findByTypeAndName(type,name);


            saveCategories.add(foodCategory);
        }

        List<UserCategory> mappings = saveCategories.stream()
                .map(cat -> new UserCategory(cat, user))   // user는 앞에서 조회된 Users
                .toList();

        userCategoryRepository.saveAll(mappings);

        List<String> regions = request.regions();
        List<LocalCategory> saveRegion =new ArrayList<>();
        if (regions == null || regions.isEmpty()){
            user.setCheckstatus(true);
            return true;
        }
        else {
            for (String raw : regions) {
                String[] parts = raw.split(":", 2);
                if (parts.length != 2) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT); // "분류:이름" 형식 아니면 에러
                String type = parts[0].trim();  // 예: "경기도"
                String name = parts[1].trim();  // 예: "수원시"
                if (type.isEmpty() || name.isEmpty()) throw new BizExceptionHandler(ErrorCode.INVALID_INPUT);

                LocalCategory region = localCategoryRepository.findByTypeAndLocalName(type, name);


                saveRegion.add(region);
            }
            List<UserLocalCategory> mapping = saveRegion.stream()
                    .map(reg->new UserLocalCategory(reg,user))
                    .toList();

            userLocalCategoryRepository.saveAll(mapping);

        }


        user.setCheckstatus(true);
        return true;
    }

    //유저 nickname, introduction 설정
    @Transactional
    public void DetailSetup(Long userId, UserDetailInfoSetupRequest request){

        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));


        user.setNickname(request.nickname());

        if(!request.introduction().isEmpty()) {
            user.setIntroduction(request.introduction());
        }
    }

    @Transactional
    public void consentCheck(Long userId){

        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        //이전에 완료 했던 사용자인지 확인
        if(user.getCheckstatus()){
            return;
        }

        //동의서 체크
        userConsentRepository.findByUserUserId(user.getUserId())
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.CONSENT_NOT_FOUND));

        // 프로필 체크
        if(user.getNickname()==null){
            throw new BizExceptionHandler(ErrorCode.DONT_HAVE_NICKNAME);
        }

        List<UserCategory> categories = userCategoryRepository.findByUserUserId(user.getUserId());
        if (categories.isEmpty()) {
            throw new BizExceptionHandler(ErrorCode.NOT_FOUND_USERCATEGORY);
        }

    }

    @Transactional(readOnly = true)
    public MypageResponse mypageLoad(Long userId){
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        long feedNum = feedRepository.countByUserUserId(user.getUserId());
        long followingNum =followsRepository.countByFollowingUserId(user.getUserId());
        long followerNum =followsRepository.countByFollowerUserId(user.getUserId());
        String userNickname=user.getNickname();
        String userIntro=user.getIntroduction();
        String profileUrl=s3Service.generatePutPresignedUrl(user.getProfileImgKey());
        List<FeedPhotosWithYearDto> thumbnailPhotos = feedPhotosRepository.findThumbnailsByUser(user);
        List<Long> feedIds = feedRepository.findFeedIdsByUserUserId(user.getUserId());
        List<UrlMappedByFeedIdDto> urlList = feedPhotosRepository.findByFeedFeedId(feedIds);

        Map<Long, String> urlMappedByFeedId = urlList.stream()
                .collect(Collectors.toMap(
                        UrlMappedByFeedIdDto::getFeedId, // 키: feedId를 추출합니다.
                        dto -> s3Service.generateGetPresignedUrl(dto.getFeedPhotos().getStorageKey()) // 값: DTO를 URL로 변환합니다.
                ));

        Map<Integer, List<String>> groupedUrlsByYear = thumbnailPhotos.stream()
                .collect(Collectors.groupingBy(
                        FeedPhotosWithYearDto::getYear,
                        // 3. 각 그룹의 FeedPhotos 객체를 S3 URL로 변환하여 리스트로 만듭니다.
                        Collectors.mapping(
                                dto -> s3Service.generateGetPresignedUrl(dto.getFeedPhoto().getStorageKey()),
                                Collectors.toList()
                        )
                ));


        MypageResponse mypageResponse =MypageResponse.builder()
                .feedNum(feedNum)
                .followingNum(followingNum)
                .followerNum(followerNum)
                .userNickname(userNickname)
                .userIntro(userIntro)
                .profileUrl(profileUrl)
                .urlGroupedByYear(groupedUrlsByYear)
                .urlMappedByFeedId(urlMappedByFeedId)
                .build();

        return mypageResponse;
    }


    // 내가 팔로우하는 사람들 (팔로잉 목록)
    @Transactional(readOnly = true)
    public Slice<GetFollowResponse> getMyFollowings(Long userId, Pageable pageable) {
        Slice<Users> slice = followsRepository.findFollowingsUsers(userId, pageable);

        return slice.map(u -> new GetFollowResponse(
                u.getNickname(),
                u.getProfileImgKey(),
                u.getIntroduction()
        ));

    }

    // 나를 팔로우하는 사람들 (팔로워 목록)
    @Transactional(readOnly = true)
    public Slice<GetFollowResponse> getMyFollowers(Long userId, Pageable pageable) {
        Slice<Users> slice = followsRepository.findFollowersUsers(userId, pageable);

        return slice.map(u -> new GetFollowResponse(
                u.getNickname(),
                u.getProfileImgKey(),
                u.getIntroduction()
        ));
    }

    @Transactional(readOnly = true)
    public PersonalInforResponse getPersonalInfor(Long userId){
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        Boolean oauthUser=true;
        UserOauth userOauth =userOauthRepository.findByUser(user);

        if(userOauth==null){
            oauthUser=false;
        }

        PersonalInforResponse response =PersonalInforResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .birthDay(user.getBirthDay())
                .createdAt(user.getCreatedAt())
                .OauthUser(oauthUser)
                .build();

        return response;
    }

//    @Transactional(readOnly = true)
//    public  Map<String, List<String>> thumbnailsSetLocal(Long userId, Pageable pageable){
//
//        Users user = usersRepository.findByUserId(userId)
//                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));
//
//        Slice<FeedPhotosWithLocalDto> thumbnailPhotos = feedPhotosRepository.findThumbnailsByUserOrderInLocal(user,pageable);
//        Map<String, List<String>> groupedUrlsByLocal =thumbnailPhotos.stream()
//                .collect(Collectors.groupingBy(
//                        FeedPhotosWithLocalDto::getLocalName,
//                        Collectors.mapping(
//                                dto -> s3Service.generateGetPresignedUrl(dto.getFeedPhoto().getStorageKey()),
//                                Collectors.toList()
//                        )
//
//                ));
//
//        return groupedUrlsByLocal;
//    }

}
