package com.meokplaylist.domain.service;

import com.meokplaylist.api.dto.auth.*;
import com.meokplaylist.domain.repository.UserOauthRepository;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.UserOauth;
import com.meokplaylist.infra.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserOauthRepository userOauthRepository;
    private final JwtTokenService jwtTokenService;

    @Transactional
    public LoginResult login(AuthLoginRequest request){

        UserOauth userOauth = userOauthRepository.findOauthWithUser(request.email(), request.providerUid())
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USEROAUTH_NOT_FOUND));
        Users user = userOauth.getUser();

        if(user.getNickname()==null){
            return LoginResult.needSignup(user.getUserId());
        }

        String newAccess = jwtTokenService.reissueAccessToken(user.getJwtRefreshToken());
        user.setJwtAccessToken(newAccess);
        return LoginResult.success(user.getJwtAccessToken());

    }

    @Transactional
    public LoginResult socialLogin(AuthSocialLoginRequest request){
        UserOauth userOauth = userOauthRepository.findOauthWithUser(request.email(), request.providerUid())
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USEROAUTH_NOT_FOUND));
        Users user = userOauth.getUser();

        if(user.getNickname()==null){
            return LoginResult.needSignup(user.getUserId());
        }

        String newAccess = jwtTokenService.reissueAccessToken(user.getJwtRefreshToken());
        user.setJwtAccessToken(newAccess);
        return LoginResult.success(user.getJwtAccessToken());

    }


    @Transactional
    public void signUp(AuthSignUpRequest request){

        Users users = Users.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .birthDay(request.birthDay())
                .build();

        usersRepository.save(users);

    }

    @Transactional
    public void emailInspect(AuthEmailInspectRequest request){
        if(usersRepository.findByEmail(request.email()).isPresent() ) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }


}