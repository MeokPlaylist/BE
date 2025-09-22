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
import com.meokplaylist.infra.user.OauthProviders;
import com.meokplaylist.infra.user.UserOauth;
import com.meokplaylist.infra.user.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    // 카카오 리소스 서버 (/v2/user/me)
    private final WebClient kakaoResource = WebClient.builder()
            .baseUrl("https://kapi.kakao.com")
            .build();

    /**
     * 이메일/비밀번호 로그인
     * - 성공 시 서비스용 JWT 토큰 페어 rotate 발급.
     */
    @Transactional
    public JwtTokenPair login(AuthLoginRequest request) {
        Users user = usersRepository.findByEmail(request.email())
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BizExceptionHandler(ErrorCode.INVALID_PASSWORD);
        }

        JwtTokenPair pair = jwtTokenService.createTokenPair(
                user.getUserId(), user.getEmail(), user.getName());

        user.setJwtAccessToken(pair.accessToken());
        user.setJwtRefreshToken(pair.refreshToken());

        return pair;
    }

    /**
     * 구글 로그인
     * - 프론트가 현재 Google ID Token만 보내는 구조이므로 social refresh는 null일 수 있음.
     * - 필요 시: Google Sign-In에서 serverAuthCode를 받아 백엔드에서 토큰 교환하여 social refresh 저장 가능(주석 위치 참조).
     */
    @Transactional
    public JwtTokenPair loginWithGoogle(String idToken/*, @Nullable String serverAuthCode */) throws Exception {
        OauthProviders googleProvider = oauthProvidersRepository.findByName("google");

        GoogleIdToken googleIdToken = googleIdTokenVerifier.verify(idToken);
        if (googleIdToken == null) {
            throw new BizExceptionHandler(ErrorCode.INVALID_GOOGLEOAUTH_TOKEN);
        }

        GoogleIdToken.Payload payload = googleIdToken.getPayload();
        String providerUid = payload.getSubject();
        String name = (String) payload.get("name");
        String email = payload.getEmail();

        // (선택) serverAuthCode가 있다면 여기서 구글 토큰 교환하여 social refresh 확보 가능
        String socialAccessToken = null;
        String socialRefreshToken = null;

        Users user;
        UserOauth userOauth;

        Optional<UserOauth> opt = userOauthRepository.findByProviderUid(providerUid);
        if (opt.isPresent()) {
            userOauth = opt.get();
            user = userOauth.getUser();
        } else {
            user = new Users(email, null, name, null);
            usersRepository.save(user);
            userOauth = new UserOauth(user, googleProvider, providerUid);
        }

        // 소셜 토큰(지금은 null 가능) 저장
        userOauth.setSocialAccessToken(socialAccessToken);
        userOauth.setSocialRefreshToken(socialRefreshToken);
        userOauthRepository.save(userOauth);

        // 서비스용 JWT 발급
        JwtTokenPair pair = jwtTokenService.createTokenPair(user.getUserId(), user.getEmail(), user.getName());
        user.setJwtAccessToken(pair.accessToken());
        user.setJwtRefreshToken(pair.refreshToken());

        return pair;
    }

    /**
     * 카카오 로그인
     * - 프론트(Kakao SDK)에서 OAuthToken(access, refresh 둘 다 가능)을 전달한다는 가정.
     * - 소셜 access/refresh는 UserOauth에 저장, 서비스용 JWT는 Users에 저장/반환.
     */
    @Transactional
    public JwtTokenPair loginWithKakao(String socialAccessTokenFromClient, String socialRefreshTokenFromClientOrNull) {
        // 1) 카카오 사용자 정보 조회
        KakaoUserMe me = kakaoResource.get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + socialAccessTokenFromClient)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new InvalidExternalTokenException("Invalid Kakao token")))
                .bodyToMono(KakaoUserMe.class)
                .block();

        String kakaoId = String.valueOf(me.getId());
        String email = me.getKakao_account() != null ? me.getKakao_account().getEmail() : null;
        String name = (me.getKakao_account() != null && me.getKakao_account().getProfile() != null)
                ? me.getKakao_account().getProfile().getNickname()
                : null;

        OauthProviders kakaoProvider = oauthProvidersRepository.findByName("kakao");

        Users user;
        UserOauth userOauth;

        Optional<UserOauth> opt = userOauthRepository.findByProviderUid(kakaoId);
        if (opt.isPresent()) {
            userOauth = opt.get();
            user = userOauth.getUser();
        } else {
            user = new Users(email, null, name, null);
            usersRepository.save(user);
            userOauth = new UserOauth(user, kakaoProvider, kakaoId);
        }

        // 2) 소셜 토큰 저장 (refresh는 null일 수 있으나, SDK가 내려줬다면 반드시 저장)
        userOauth.setSocialAccessToken(socialAccessTokenFromClient);
        if (socialRefreshTokenFromClientOrNull != null && !socialRefreshTokenFromClientOrNull.isBlank()) {
            userOauth.setSocialRefreshToken(socialRefreshTokenFromClientOrNull);
        }
        userOauthRepository.save(userOauth);

        // 3) 서비스용 JWT 발급/저장 → 클라이언트로 반환
        JwtTokenPair pair = jwtTokenService.createTokenPair(user.getUserId(), user.getEmail(), user.getName());
        user.setJwtAccessToken(pair.accessToken());
        user.setJwtRefreshToken(pair.refreshToken());

        return pair;
    }

    /**
     * 회원가입 → 서비스용 JWT 페어 발급/저장 후 반환
     */
    @Transactional
    public JwtTokenPair signUp(AuthSignUpRequest request) {
        Users users = Users.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .birthDay(request.birthDay())
                .build();

        usersRepository.save(users);

        Users savedUser = usersRepository.findByEmailAndPasswordHashIsNotNull(request.email())
                .orElseThrow(() -> new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        JwtTokenPair pair = jwtTokenService.createTokenPair(
                savedUser.getUserId(), savedUser.getEmail(), savedUser.getName());

        savedUser.setJwtAccessToken(pair.accessToken());
        savedUser.setJwtRefreshToken(pair.refreshToken());

        return pair;
    }

    @Transactional
    public Boolean emailInspect(AuthEmailInspectRequest request) {
        return usersRepository.findByEmailAndPasswordHashIsNotNull(request.email()).isEmpty();
    }

    @Transactional
    public Boolean nicknameDuplicateCheck(String nickname) {
        return usersRepository.findByNickname(nickname).isEmpty();
    }
}
