package com.meokplaylist.infra;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

import java.net.URL;

public class KakaoIdTokenProvider {

    private static final String JWK_URL = "https://kauth.kakao.com/.well-known/jwks.json";
    private static final String ISSUER = "https://kauth.kakao.com";

    private static final String CLIENT_ID = "YOUR_KAKAO_REST_API_KEY"; //설정을 구글 처럼 변경할것

    public JWTClaimsSet getClaims(String idToken) throws Exception {
        ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        JWKSource<SecurityContext> keySource = new RemoteJWKSet<>(new URL(JWK_URL));
        JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        JWTClaimsSet claims = jwtProcessor.process(idToken, null);

        // 보안 검증
        if (!claims.getIssuer().equals(ISSUER)) {
            throw new IllegalArgumentException("Issuer mismatch");
        }
        if (!claims.getAudience().contains(CLIENT_ID)) {
            throw new IllegalArgumentException("Audience mismatch");
        }

        return claims;
    }
}
