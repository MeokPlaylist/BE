package com.meokplaylist.security;

import org.springframework.http.HttpHeaders;
import com.meokplaylist.api.dto.KakaoSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KakaoLocalClient {

    private final WebClient webClient = WebClient.builder().build();

    private static int RADIUS = 17; // 17m로 설정 적당한 거리일듯

    @Value("${kakao.local.base-url}")
    private String baseUrl;

    @Value("${kakao.local.rest-api-key}")
    private String restApiKey;

    public KakaoSearchResponse searchByCategory(String category, double x, double y,
                                                Integer page, Integer size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(baseUrl.replace("https://", "").replace("http://",""))
                        .path("/v2/local/search/category.json")
                        .queryParam("category_group_code", category) // ex) FD6:음식점, CE7:카페
                        .queryParam("x", x)
                        .queryParam("y", y)
                        .queryParam("radius", RADIUS)                // 0~20000 (m)
                        .queryParamIfPresent("page", Optional.ofNullable(page)) // 1~45
                        .queryParamIfPresent("size", Optional.ofNullable(size)) // 1~15
                        .queryParam("sort","distance") //거리 순으로 정렬
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + restApiKey)
                .retrieve()
                .bodyToMono(KakaoSearchResponse.class)
                .block();
    }
}
