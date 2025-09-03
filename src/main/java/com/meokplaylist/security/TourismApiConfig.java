package com.meokplaylist.security;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;

import java.time.Duration;

@Configuration
public class TourismApiConfig {


    @Value("${tourapi.base-url}") private String baseUrl;

    @Value("${tourapi.service-key}") private String serviceKey;

    @Value("${tourapi.mobile-os:ETC}") private String mobileOs;

    @Value("${tourapi.mobile-app:MeokPlaylist}") private String mobileApp;

    @Value("${tourapi.default-type:json}") private String defaultType;

    @Value("${tourapi.connect-timeout-ms:2000}") private int connectTimeoutMs;

    @Value("${tourapi.read-timeout-ms:3000}") private int readTimeoutMs;

    @Bean("tourWebClient")
    public WebClient tourApiWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .responseTimeout(Duration.ofMillis(readTimeoutMs));

        // 공통 쿼리 파라미터(필수: MobileOS, MobileApp, 타입 등)와 키를 기본으로 붙여줌
        ExchangeFilterFunction addDefaultParams = (req, next) -> {
            UriBuilderFactory factory = new DefaultUriBuilderFactory(baseUrl);
            URI newUri = factory.builder()
                    .path(req.url().getPath())
                    .replaceQuery(req.url().getQuery()) // 기존 쿼리 유지
                    .queryParam("MobileOS", mobileOs)
                    .queryParam("MobileApp", mobileApp)
                    .queryParam("_type", defaultType)
                    .queryParam("serviceKey", serviceKey)
                    .build();
            ClientRequest newReq = ClientRequest.from(req).url(newUri).build();
            return next.exchange(newReq);
        };

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(addDefaultParams)
                .filter(ExchangeFilterFunction.ofResponseProcessor(resp -> {
                    if (resp.statusCode().isError()) {
                        return Mono.error(new IllegalStateException("TourAPI error: " + resp.statusCode()));
                    }
                    return Mono.just(resp);
                }))
                .build();
    }
}

