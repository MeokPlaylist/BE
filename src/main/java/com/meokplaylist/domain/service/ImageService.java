package com.meokplaylist.domain.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3Client;
    @Value("${cloud.ncp.object-storage.bucket}")
    private String bucketName;


    @Transactional
    public String uploadProfileImage(MultipartFile file, Long userId) throws IOException {

        if (file == null || file.isEmpty()) {
            return null;
        }

        if (file.isEmpty() || !file.getContentType().startsWith("image/"))
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");

        String ext = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")); //원본 파일 마지막 이름 뽑아내기
        String key = "profiles/user_" + userId + "_" + UUID.randomUUID() + ext;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName) //저장 버킷 이름
                .key(key) // 저장 위치 경로
                .acl("public-read") //공개 읽기 권한
                .contentType(file.getContentType())
                .build();

       PutObjectResponse response = s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        if (response.sdkHttpResponse().isSuccessful()) {
            System.out.println("업로드 성공! ETag: " + response.eTag());
        } else {
            System.out.println(" 업로드 실패! 상태 코드: " + response.sdkHttpResponse().statusCode());
        }
        //디버깅을 위한 코드

        return "https://" + bucketName + ".kr.object.ncloudstorage.com/" + key;
        // "https://kr.object.ncloudstorage.com/" + bucketName + "/" + key; 공식 표기 위에 것이 안되면 해보자,,
    }
}
