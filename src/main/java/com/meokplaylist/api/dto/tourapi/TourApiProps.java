package com.meokplaylist.api.dto.tourapi;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tourapi")
public record TourApiProps(

        String baseUrl,
        String serviceKey
) { }
