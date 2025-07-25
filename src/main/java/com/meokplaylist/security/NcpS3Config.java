package com.meokplaylist.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class NcpS3Config {

    @Value("${cloud.ncp.object-storage.access-key}")
    private String accessKey;

    @Value("${cloud.ncp.object-storage.secret-key}")
    private String secretKey;

    @Value("${cloud.ncp.object-storage.endpoint}")
    private String endpoint;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials creds = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("kr-standard"))  // 네이버 region
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .build();
    }
}
