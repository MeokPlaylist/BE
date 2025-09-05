package com.meokplaylist.domain.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    private static final String BASE_PROFILE_FMG="https://kr.object.ncloudstorage.com/meokplaylist/%EA%B8%B0%EB%B3%B8%20%ED%94%84%EB%A1%9C%ED%95%84.png";

    @Value("${cloud.ncp.object-storage.bucket}")
    private String bucketName;



    private String extractKeyFromUrl(String url) {
        if (url == null || !url.contains(".com/")) return null;
        return url.substring(url.indexOf(".com/") + 5); // ".com/" 이후부터 끝까지
    }

    @Transactional(readOnly = true)
    public String generatePutPresignedUrl(String fileKey) {
        // fileKey가 없거나 비어있으면 기본 이미지 URL 반환
        if (fileKey == null || fileKey.isBlank()) {
            return BASE_PROFILE_FMG;
        }

        try {
            // 1. Presign 할 GetObject 요청 객체 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            // 2. Presign 요청 객체 생성 (유효 시간 설정 - 예: 10분)
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10)) // URL 유효 시간
                    .putObjectRequest(putObjectRequest)
                    .build();

            // 3. Presigner를 통해 URL 생성
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            return presignedRequest.url().toString();

        } catch (Exception e) {
            return null; // 에러 발생 시 URL을 반환하지 않음
        }
    }

    @Transactional(readOnly = true)
    public String generateGetPresignedUrl(String fileKey) {
        // fileKey가 없거나 비어있으면 기본 이미지 URL 반환
        if (fileKey == null || fileKey.isBlank()) {
            return BASE_PROFILE_FMG;
        }

        try {
            // 1. Presign 할 GetObject 요청 객체 생성
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            // 2. Presign 요청 객체 생성 (유효 시간 설정 - 예: 10분)
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10)) // URL 유효 시간
                    .getObjectRequest(getObjectRequest)
                    .build();

            // 3. Presigner를 통해 URL 생성
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toString();

        } catch (Exception e) {
            return null; // 에러 발생 시 URL을 반환하지 않음
        }
    }
}
