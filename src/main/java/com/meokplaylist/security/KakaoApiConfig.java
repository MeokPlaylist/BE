package com.meokplaylist.security;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.support.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class KakaoApiConfig {
    @Value("${kakao.local.base-url}") String baseUrl;  // e.g. https://dapi.kakao.com
    @Value("${kakao.local.rest-api-key}") String apiKey;

    @Bean("kakaoWebClient")
    public WebClient kakaoWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl) // 경로는 .path(...)만 쓰면 됨
                .defaultHeader(HttpHeaders.AUTHORIZATION, "KakaoAK " + apiKey)
                .build();
    }
}
