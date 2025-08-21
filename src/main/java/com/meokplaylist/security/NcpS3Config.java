package com.meokplaylist.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

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
        S3Configuration s3conf = S3Configuration.builder()
                .pathStyleAccessEnabled(true) // ★ NCP 필수에 가깝게 권장
                .build();

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of("kr-standard"))  // 네이버 region
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .serviceConfiguration(s3conf)
                .build();
    }

    @Bean // 이 메서드가 반환하는 객체를 Spring Bean으로 등록합니다.
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .endpointOverride(URI.create("https://kr.object.ncloudstorage.com"))
                .region(Region.of("kr-standard"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();
    }
}
