package com.meokplaylist.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import com.meokplaylist.api.dto.KakaoSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;


import java.util.Optional;

@Component
public class KakaoLocalClient {

    private final WebClient wc;
    public KakaoLocalClient(@Qualifier("kakaoWebClient") WebClient wc) {
        this.wc = wc;
    }

    private static int RADIUS = 1000; //3m //테스트 일떄는 1km로 설정

    @Value("${kakao.local.base-url}")
    private String baseUrl;

    @Value("${kakao.local.rest-api-key}")
    private String restApiKey;


    public KakaoSearchResponse searchByCategory(String category, double x, double y,
                                                Integer page, Integer size) {
        return wc.get()
                .uri(b -> b.path("/v2/local/search/category.json")
                        .queryParam("category_group_code", category)
                        .queryParam("x", x)
                        .queryParam("y", y)
                        .queryParam("radius", RADIUS)
                        .queryParamIfPresent("page", Optional.ofNullable(page))
                        .queryParamIfPresent("size", Optional.ofNullable(size))
                        .queryParam("sort","distance")
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new ResponseStatusException(
                                        resp.statusCode(), "Kakao API error: " + body))))
                .bodyToMono(KakaoSearchResponse.class)
                .block();
    }

}
