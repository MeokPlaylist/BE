package com.meokplaylist.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.meokplaylist.api.dto.JwtTokenPair;
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

    public String createToken(Long id, String email, String nickname, String socialTokenId, Duration ttl,String tokenType){
        Instant now = Instant.now();
        Instant exp = now.plus(ttl);

        return JWT.create()
                .withSubject(String.valueOf(id))
                .withJWTId(UUID.randomUUID().toString())       // 토큰 id
                .withIssuedAt(Date.from(now))        // 발급 시간
                .withClaim("email",email)
                .withClaim("nickname",nickname)
                .withClaim("socialTokenId",socialTokenId)
                .withClaim("tokenType",tokenType)
                .withExpiresAt(Date.from(exp))                 // 토큰 만료 일자
                .sign(algorithm());

    }

    public JwtTokenPair createTokenPair(Long id, String email, String nickname, String socialTokenId){
        String accessToken = createToken(id,email,nickname,socialTokenId,accessTokenExpMinutes,"accessToken");
        String refreshToken = createToken(id,email,nickname,socialTokenId,refreshTokenExpMinutes,"refreshToken");

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
