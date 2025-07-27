package com.meokplaylist.domain.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.meokplaylist.api.dto.auth.*;
import com.meokplaylist.domain.repository.UserOauthRepository;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.KakaoIdTokenProvider;
import com.meokplaylist.infra.OauthProviders;
import com.meokplaylist.infra.UserOauth;
import com.meokplaylist.infra.Users;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserOauthRepository userOauthRepository;
    private final JwtTokenService jwtTokenService;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final KakaoIdTokenProvider kakaoIdTokenProvider;

    @Transactional
    public String login(AuthLoginRequest request){
        Users user = usersRepository.findByEmailAndPasswordHash(request.email(),request.password())
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        // 여기서 암호 비교
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BizExceptionHandler(ErrorCode.INVALID_PASSWORD); // 필요 시 새 에러코드 생성
        }

        String setAccess = jwtTokenService.reissueAccessToken(user.getJwtRefreshToken());
        user.setJwtAccessToken(setAccess);
        return user.getJwtAccessToken();

    }

    @Transactional
    public String socialLogin(AuthSocialLoginRequest request) throws Exception {
        String providerUid=null;
        String name=null;
        String email = null;
        OauthProviders oauthProviders=null;

        if(request.provider().equals("google")){

            GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(request.idToken());
            if (googleIdToken == null) {
                throw new BizExceptionHandler(ErrorCode.INVALID_GOOGLEOAUTH_TOKEN); // 직접 예외 처리
            }

            GoogleIdToken.Payload payload=googleIdToken.getPayload();
            providerUid =payload.getSubject();
            name=(String)payload.get("name");
            email=payload.getEmail();
            oauthProviders.setProviderId(1);
            oauthProviders.setName(request.provider());

            Users user=new Users(email,null,name,null);
            usersRepository.save(user);


        } else if (request.provider().equals("kakao")) { //kakao는 토큰 검사를 권장하지 않아서 google처럼 처리는 안함

            JWTClaimsSet claim =kakaoIdTokenProvider.getClaims(request.idToken());

            providerUid =claim.getSubject();
            name=(String)claim.getClaim("nickname");
            email=(String) claim.getClaim("email");
            oauthProviders.setProviderId(2);
            oauthProviders.setName(request.provider());

            Users user=new Users(email,null,name,null);
            usersRepository.save(user);

        } else {
            throw new BizExceptionHandler(ErrorCode.INVALID_OAUTH_TOKEN);
        }

        Optional<UserOauth> OptionalUserOauth=userOauthRepository.findByProviderUid(providerUid);

        if(OptionalUserOauth.isPresent()){ //소셜 로그인시 DB에 유저가 저장되어 있다면
            UserOauth userOauth=OptionalUserOauth.get();

            if(!userOauth.getRefreshToken().isEmpty()){ //refresh 토큰이 존재한다면
                String setAccess = jwtTokenService.reissueAccessToken(userOauth.getUser().getJwtRefreshToken());
                userOauth.getUser().setJwtAccessToken(setAccess);
                return userOauth.getUser().getJwtAccessToken();
            }
            else{
                throw new BizExceptionHandler(ErrorCode.NOT_HAVE_REFRESHTOKEN);
            }
        }
        else{
            Users user=usersRepository.findByEmailAndPasswordHash(email,null)
                    .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

            UserOauth userOauth=new UserOauth(user,oauthProviders,providerUid);
            JwtTokenPair token=jwtTokenService.createTokenPair(user.getUserId(),user.getEmail(),user.getName());

            user.setJwtAccessToken(token.accessToken());
            user.setJwtRefreshToken(token.refreshToken());
            return user.getJwtAccessToken();
        }


    }


    @Transactional
    public String signUp(AuthSignUpRequest request){

        Users users = Users.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .birthDay(request.birthDay())
                .build();

        usersRepository.save(users);

        Optional<Users> OptionalUser  =  usersRepository.findByEmail(request.email());
        Users savedUser =OptionalUser.get();

       JwtTokenPair tokens =jwtTokenService.createTokenPair(savedUser.getUserId(),savedUser.getEmail(),savedUser.getName());
       savedUser.setJwtRefreshToken(tokens.refreshToken());
       savedUser.setJwtAccessToken(tokens.accessToken());

       return savedUser.getJwtAccessToken();

    }

    @Transactional
    public Boolean emailInspect(AuthEmailInspectRequest request){
        if(usersRepository.findByEmail(request.email()).isPresent() ) {
            return false;
        }
        return true;
    }

}