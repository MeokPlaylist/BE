package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.BooleanRequest;
import com.meokplaylist.api.dto.category.CategorySetUpRequest;
import com.meokplaylist.api.dto.user.FindUserRequest;
import com.meokplaylist.api.dto.user.NewPasswordRequest;
import com.meokplaylist.domain.repository.UserConsentRepository;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.domain.repository.category.CategoryRepository;
import com.meokplaylist.domain.repository.category.LocalCategoryRepository;
import com.meokplaylist.domain.repository.category.UserCategoryRepository;
import com.meokplaylist.domain.repository.category.UserLocalCategoryRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.Category.Category;
import com.meokplaylist.infra.Category.LocalCategory;
import com.meokplaylist.infra.Category.UserCategory;
import com.meokplaylist.infra.Category.UserLocalCategory;
import com.meokplaylist.infra.UserConsent;
import com.meokplaylist.infra.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConsentRepository userConsentRepository;
    private  final CategoryRepository categoryRepository;
    private final LocalCategoryRepository localCategoryRepository;
    private final UserCategoryRepository userCategoryRepository;
    private final UserLocalCategoryRepository userLocalCategoryRepository;
    private static String consentFileUrl ="https://kr.object.ncloudstorage.com/meokplaylist/%EB%A8%B9%ED%94%8C%EB%A6%AC%20%EB%8F%99%EC%9D%98%EC%84%9C%20%EB%82%B4%EC%9A%A9.txt";


    public Long findUser(FindUserRequest request){
        Users user = usersRepository.findByEmailAndPasswordHashIsNotNull(request.email())
                        .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));


        return user.getUserId();
    }

    @Transactional
    public Boolean newPassword(NewPasswordRequest request){
        Users user =usersRepository.findByUserId(request.userId())
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        return true;
    }

    @Transactional
    public Boolean consentUpload(BooleanRequest request, Long userId){

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

    @Transactional
    public void categorySetUp(CategorySetUpRequest request, Long userId) {
        // 1. 유저 조회
        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        // 2. 카테고리 이름 목록 가져오기
        List<String> categoryFoodNames = request.categoryFoodNames();  // ex) ["분위기:로맨틱", "음식:한식"]
        List<String> categoryLocalNames = request.categoryLocalNames();

        // 3. 이름으로 카테고리 엔티티 조회
        List<Category> foodCategories = categoryRepository.findAllByNameIn(categoryFoodNames);

        if (foodCategories.isEmpty()){
            throw new BizExceptionHandler(ErrorCode.CATEGORY_NOT_FOUND);
        }

        // 4. 매핑 저장
        for (Category category : foodCategories) {

            UserCategory userCategory = new UserCategory(category,user);
            userCategoryRepository.save(userCategory);
        }

        if(!categoryLocalNames.isEmpty()){

            List<LocalCategory> localCategories= localCategoryRepository.findAllByLocalNameIn(categoryLocalNames);
            for (LocalCategory localCategory : localCategories) {

                UserLocalCategory userLocalCategory = new UserLocalCategory(localCategory,user);

                userLocalCategoryRepository.save(userLocalCategory);
            }
        }



    }

    public void consentCheck(Long userId){

        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        //이전에 완료 했던 사용자인지 확인
        if(user.getCheckstatus()){
            throw new BizExceptionHandler(ErrorCode.CHECH_OK);
        }

        //동의서 체크
        userConsentRepository.findByUserUserId(user.getUserId())
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.CONSENT_NOT_FOUND));

        // 프로필 체크
        if(user.getNickname().isEmpty()){
            throw new BizExceptionHandler(ErrorCode.DONT_HAVE_NICKNAME);
        }

        //카테고리 체크
        userCategoryRepository.findByUserUserId(user.getUserId())
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USERCATEGORY_NOT_FONUD));

    }

}
