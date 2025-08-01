package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.BooleanRequest;
import com.meokplaylist.api.dto.category.CategorySetUpRequest;
import com.meokplaylist.api.dto.user.UserNewPasswordRequest;
import com.meokplaylist.domain.repository.UserConsentRepository;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.domain.repository.category.FoodCategoryRepository;
import com.meokplaylist.domain.repository.category.LocalCategoryRepository;
import com.meokplaylist.domain.repository.category.UserCategoryRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.Category.FoodCategory;
import com.meokplaylist.infra.Category.LocalCategory;
import com.meokplaylist.infra.Category.UserCategory;
import com.meokplaylist.infra.UserConsent;
import com.meokplaylist.infra.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserConsentRepository userConsentRepository;
    private  final FoodCategoryRepository foodCategoryRepository;
    private final LocalCategoryRepository localCategoryRepository;
    private final UserCategoryRepository userCategoryRepository;
    private static String consentFileUrl ="https://kr.object.ncloudstorage.com/meokplaylist/%EB%A8%B9%ED%94%8C%EB%A6%AC%20%EB%8F%99%EC%9D%98%EC%84%9C%20%EB%82%B4%EC%9A%A9.txt";


    @Transactional
    public Boolean newPassword(UserNewPasswordRequest request, Long userId){
        Users user = usersRepository.findByUserIdAndNameAndEmailAndBirthDay(userId, request.name(),request.email() ,request.birthDay())
                        .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        user.setPasswordHash(passwordEncoder.encode(request.password()));

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
    public void categorySetUp(CategorySetUpRequest request, Long userId){
        Users user =usersRepository.findByUserId(userId)
                .orElseThrow(()-> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));
        FoodCategory foodCategory =new FoodCategory(
                request.moodBigObject(),
                request.moodSmallObject(),
                request.foodBigObject(),
                request.foodSmallObject(),
                request.companionBigObject(),
                request.companionSmallObject()
        );
        foodCategoryRepository.save(foodCategory);
        UserCategory userCategory=null;
        if(!request.localBigObject().isEmpty() && !request.localSmallObject().isEmpty()){
            LocalCategory localCategory =new LocalCategory(request.localBigObject(), request.localSmallObject());
            localCategoryRepository.save(localCategory);
            userCategory=new UserCategory(user,foodCategory,localCategory);

        }
        else{
            userCategory=new UserCategory(user,foodCategory);
        }

        userCategoryRepository.save(userCategory);

    }
}
