package com.meokplaylist.security;

import com.meokplaylist.api.dto.tourapi.PetTourApiProps;
import com.meokplaylist.api.dto.tourapi.TourApiProps;
import  io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;

import java.time.Duration;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class TourismApiConfig {

    private final TourApiProps tourApiProps;
    private final PetTourApiProps petTourApiProps;

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

        ExchangeFilterFunction addDefaultParams = (req, next) -> {
            // 기존 요청 URI를 기반으로 새로운 URI를 만듭니다.
            URI newUri = UriComponentsBuilder.fromUri(req.url())
                    .queryParam("MobileOS", mobileOs)
                    .queryParam("MobileApp", mobileApp)
                    .queryParam("_type", defaultType)
                    .queryParam("serviceKey", tourApiProps.serviceKey())
                    .build(true) // 인코딩 처리를 위임
                    .toUri();

            ClientRequest newReq = ClientRequest.from(req).url(newUri).build();
            return next.exchange(newReq);
        };

        return WebClient.builder()
                .baseUrl(tourApiProps.baseUrl())
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
    /*
    @Bean("petTourWebClient")
    public WebClient petTourApiWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .responseTimeout(Duration.ofMillis(readTimeoutMs));

        ExchangeFilterFunction addDefaultParams = (req, next) -> {
            // 기존 요청 URI를 기반으로 새로운 URI를 만듭니다.
            URI newUri = UriComponentsBuilder.fromUri(req.url())
                    .queryParam("MobileOS", mobileOs)
                    .queryParam("MobileApp", mobileApp)
                    .queryParam("_type", defaultType)
                    .queryParam("serviceKey", petTourApiProps.serviceKey())
                    .build(true) // 인코딩 처리를 위임
                    .toUri();

            ClientRequest newReq = ClientRequest.from(req).url(newUri).build();
            return next.exchange(newReq);
        };

        return WebClient.builder()
                .baseUrl(petTourApiProps.baseUrl())
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
    */

}

