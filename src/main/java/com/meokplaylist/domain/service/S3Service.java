package com.meokplaylist.domain.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private S3Client s3;
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


    @Transactional
    public void deleteOne(String key) {

        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());

        // 3) DB에서 해당 키 참조 해제/삭제
        //    - 여기서 “저장소 삭제 실패 → DB만 지워짐” 같은 역전 위험이 있어
        //      아래 ‘트랜잭션/아웃박스’ 섹션 참고
    }

    /** 여러 개 한 번에 삭제 */
    @Transactional
    public void deleteMany(List<String> keys) {
        // 권한/참조 검증 생략 금지

        var objects = keys.stream()
                .map(k -> ObjectIdentifier.builder().key(k).build())
                .collect(Collectors.toList());

        s3.deleteObjects(DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(d -> d.objects(objects).quiet(true)) // quiet=true면 없는 키도 조용히 넘어감(멱등성↑)
                .build());

        // DB 반영
    }

    /** “폴더처럼” 프리픽스 기준 일괄 삭제 (예: photos/123/) */
    @Transactional
    public void deleteByPrefix(String prefix) {
        String continuationToken = null;
        do {
            ListObjectsV2Response list = s3.listObjectsV2(ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .continuationToken(continuationToken)
                    .build());

            var keys = list.contents().stream().map(o -> o.key()).toList();
            if (!keys.isEmpty()) {
                deleteMany(keys);
            }
            continuationToken = list.nextContinuationToken();
        } while (continuationToken != null);
    }
}
