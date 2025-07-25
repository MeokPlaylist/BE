/*package com.meokplaylist.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class GoogleConfig {

    @Value("${oauth.google.client-id}")
    private String clientId;

    @Bean
    public GoogleIdTokenVerifier googleVerifier() {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(List.of(clientId))   // aud 검증
                .build();
    }
}
*/