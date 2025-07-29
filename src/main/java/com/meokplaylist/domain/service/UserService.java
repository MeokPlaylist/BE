package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.user.UserNewPasswordRequest;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
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


    @Transactional
    public Boolean newPassword(UserNewPasswordRequest request, Long userId){
        Users user = usersRepository.findByUserIdAndNameAndEmailAndBirthDay(userId, request.name(),request.email() ,request.birthDay())
                        .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        user.setPasswordHash(passwordEncoder.encode(request.password()));

        return true;
    }
}
