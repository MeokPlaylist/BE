package com.meokplaylist.domain.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
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
    public String login(AuthLoginRequest request){
        Users user = usersRepository.findByEmail(request.email())
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        // 여기서 암호 비교
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BizExceptionHandler(ErrorCode.INVALID_PASSWORD); // 필요 시 새 에러코드 생성
        }
        /*
        String newAccess = jwtTokenService.reissueAccessToken(user.getJwtRefreshToken());
        user.setJwtAccessToken(newAccess);
        return user.getJwtAccessToken();
        */
        return "123";
    }

    @Transactional
    public void socialLogin(AuthSocialLoginRequest request){
        /*
        if(request.provider().equals("google")){
            GoogleIdToken idTokenObj = google
            if (idTokenObj == null) {
                throw new BizExceptionHandler(ErrorCode.INVALID_OAUTH_TOKEN); // 직접 예외 처리
            }

            GoogleIdToken.Payload payload = idTokenObj.getPayload();   // ← 여기서 email·sub 꺼냄
            String providerUid = payload.getSubject();
            String email       = payload.getEmail();
        }
        UserOauth userOauth = userOauthRepository.findOauthWithUser(request.idToken(), request.providerUid())
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USEROAUTH_NOT_FOUND));
        Users user = userOauth.getUser();

        String newAccess = jwtTokenService.reissueAccessToken(user.getJwtRefreshToken());
        user.setJwtAccessToken(newAccess);
        return LoginResult.success(user.getJwtAccessToken());
        */
        if(request.idToken()!=null|| request.provider() !=null){

        }
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