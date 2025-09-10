package com.meokplaylist.api.dto.tourapi;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tourapi2")
public record PetTourApiProps(
        String baseUrl,
        String serviceKey
) { }