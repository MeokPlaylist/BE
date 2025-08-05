package com.meokplaylist.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.meokplaylist.api.dto.auth.JwtTokenPair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private static Duration accessTokenExpMinutes = Duration.ofMinutes(60);
    private static Duration refreshTokenExpMinutes=Duration.ofDays(14); //기간 14일

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public String createToken(Long id, String email, String name, Duration ttl,String tokenType){
        Instant now = Instant.now();
        Instant exp = now.plus(ttl);

        return JWT.create()
                .withSubject(String.valueOf(id))
                .withJWTId(UUID.randomUUID().toString())       // 토큰 id
                .withIssuedAt(Date.from(now))        // 발급 시간
                .withClaim("email",email)
                .withClaim("name",name)
                .withClaim("tokenType",tokenType)
                .withExpiresAt(Date.from(exp))                 // 토큰 만료 일자
                .sign(algorithm());

    }

    public String reissueAccessToken(String refreshToken) {
        // 1) refresh 검증
        DecodedJWT decoded = verify(refreshToken);

        // 2) tokenType 확인
        if (!"refreshToken".equals(decoded.getClaim("tokenType").asString())) {
            throw new IllegalArgumentException("Not a refresh token");
        }

        // 3) 필요 정보 추출
        Long   id       = Long.valueOf(decoded.getSubject());
        String email    = decoded.getClaim("email").asString();
        String name = decoded.getClaim("name").asString();
        String refreshJti = decoded.getId();          // parent 식별자용

        // 4) access 토큰 새로 발급 (parentJti 포함)
        Instant now = Instant.now();
        Instant exp = now.plus(accessTokenExpMinutes);

        return JWT.create()
                .withSubject(String.valueOf(id))
                .withClaim("userId", id)
                .withJWTId(UUID.randomUUID().toString())
                .withClaim("email", email)
                .withClaim("name", name)
                .withClaim("tokenType", "accessToken")
                .withClaim("parentJti", refreshJti)   // ← 연관성
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .sign(algorithm());
    }

    public JwtTokenPair createTokenPair(Long id, String email, String name){
        String accessToken = createToken(id,email,name,accessTokenExpMinutes,"accessToken");
        String refreshToken = createToken(id,email,name,refreshTokenExpMinutes,"refreshToken");

        return JwtTokenPair.of(accessToken,refreshToken);
    }

    public DecodedJWT verify(String token) {
        return JWT.require(algorithm())
                .build()
                .verify(token);          // 예외 발생 시 401 처리
    }

    private Algorithm algorithm() {
        return Algorithm.RSA256(publicKey, privateKey);
    }
}
