package com.meokplaylist.domain.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.meokplaylist.api.dto.auth.*;
import com.meokplaylist.domain.repository.OauthProvidersRepository;
import com.meokplaylist.domain.repository.UserOauthRepository;
import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.exception.InvalidExternalTokenException;
import com.meokplaylist.infra.OauthProviders;
import com.meokplaylist.infra.UserOauth;
import com.meokplaylist.infra.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
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
    private final OauthProvidersRepository oauthProvidersRepository;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;
    private final WebClient kakao = WebClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .build();

    @Transactional
    public String login(AuthLoginRequest request){
        Users user = usersRepository.findByEmail(request.email())
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
    public AuthJwtResponse loginWithGoogle(String token) throws Exception {
        String providerUid=null;
        String name=null;
        String email = null;

        // OAuth provider에서 provier 가져오기
        OauthProviders oauthProvider = oauthProvidersRepository.findByName("google");

        GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(token);
        if (googleIdToken == null) {
            throw new BizExceptionHandler(ErrorCode.INVALID_GOOGLEOAUTH_TOKEN);
        }

        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        providerUid = payload.getSubject();
        name = (String) payload.get("name");
        email = payload.getEmail();

        Optional<UserOauth> OptionalUserOauth = userOauthRepository.findByProviderUid(providerUid);

        if (OptionalUserOauth.isPresent()) {
            UserOauth userOauth = OptionalUserOauth.get();

            if (!userOauth.getRefreshToken().isEmpty()) {
                String setAccess = jwtTokenService.reissueAccessToken(userOauth.getUser().getJwtRefreshToken());
                userOauth.getUser().setJwtAccessToken(setAccess);
                return new AuthJwtResponse(setAccess);
            } else {
                throw new BizExceptionHandler(ErrorCode.NOT_HAVE_REFRESHTOKEN);
            }

        } else {
            // 새 유저 생성
            Users user = new Users(email, null, name, null);
            usersRepository.save(user);

            UserOauth userOauth = new UserOauth(user, oauthProvider, providerUid);
            JwtTokenPair jwtToken = jwtTokenService.createTokenPair(user.getUserId(), user.getEmail(), user.getName());

            user.setJwtAccessToken(jwtToken.accessToken());
            user.setJwtRefreshToken(jwtToken.refreshToken());
            userOauth.setAccessToken(jwtToken.accessToken());
            userOauth.setRefreshToken(jwtToken.refreshToken());

            userOauthRepository.save(userOauth);
            return new AuthJwtResponse(user.getJwtAccessToken());
        }
    }

    public AuthJwtResponse loginWithKakao(String accessToken) {
        KakaoUserMe me = kakao.get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new InvalidExternalTokenException("Invalid Kakao token")))
                .bodyToMono(KakaoUserMe.class)
                .block();

        String kakaoId = String.valueOf(me.getId());
        String email = me.getKakao_account() != null ? me.getKakao_account().getEmail() : null;
        String name = (me.getKakao_account() != null && me.getKakao_account().getProfile() != null)
                ? me.getKakao_account().getProfile().getNickname() : null;

        OauthProviders oauthProvider = oauthProvidersRepository.findByName("kakao");

        Optional<UserOauth> OptionalUserOauth = userOauthRepository.findByProviderUid(kakaoId);

        if (OptionalUserOauth.isPresent()) {
            UserOauth userOauth = OptionalUserOauth.get();

            if (!userOauth.getRefreshToken().isEmpty()) {
                String setAccess = jwtTokenService.reissueAccessToken(userOauth.getUser().getJwtRefreshToken());
                userOauth.getUser().setJwtAccessToken(setAccess);
                return new AuthJwtResponse(setAccess);
            } else {
                throw new BizExceptionHandler(ErrorCode.NOT_HAVE_REFRESHTOKEN);
            }

        } else {
            // 새 유저 생성
            Users user = new Users(email, null, name, null);
            usersRepository.save(user);

            UserOauth userOauth = new UserOauth(user, oauthProvider, kakaoId);
            JwtTokenPair jwtToken = jwtTokenService.createTokenPair(user.getUserId(), user.getEmail(), user.getName());

            user.setJwtAccessToken(jwtToken.accessToken());
            user.setJwtRefreshToken(jwtToken.refreshToken());
            userOauth.setAccessToken(jwtToken.accessToken());
            userOauth.setRefreshToken(jwtToken.refreshToken());

            userOauthRepository.save(userOauth);
            return new AuthJwtResponse(user.getJwtAccessToken());
        }
    }


    @Transactional
    public String signUp(AuthSignUpRequest request){

        Users users = Users.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .birthDay(request.birthDay())
                .checkstatus(Boolean.FALSE)
                .build();

        usersRepository.save(users);

        Optional<Users> OptionalUser  =  usersRepository.findByEmailAndPasswordHashIsNotNull(request.email());
        Users savedUser =OptionalUser.get();

       JwtTokenPair tokens =jwtTokenService.createTokenPair(savedUser.getUserId(),savedUser.getEmail(),savedUser.getName());
       savedUser.setJwtRefreshToken(tokens.refreshToken());
       savedUser.setJwtAccessToken(tokens.accessToken());

       return savedUser.getJwtAccessToken();

    }

    @Transactional
    public Boolean emailInspect(AuthEmailInspectRequest request){
        if(usersRepository.findByEmailAndPasswordHashIsNotNull(request.email()).isPresent() ) {
            return false;
        }
        return true;
    }


}