package com.meokplaylist.domain.service;

import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.user.Users;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class S3Service {
    private static final String BASE_PROFILE_FMG="https://kr.object.ncloudstorage.com/meokplaylist/%EA%B8%B0%EB%B3%B8%20%ED%94%84%EB%A1%9C%ED%95%84.png";

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final UsersRepository usersRepository;

    @Value("${cloud.ncp.object-storage.bucket}")
    private String bucketName;



    @Transactional
    public void uploadProfileImage(MultipartFile file, Long userId) throws IOException {

        if (file.isEmpty() || !file.getContentType().startsWith("image/"))
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");

        Users user = usersRepository.findByUserId(userId)
                .orElseThrow(()->new BizExceptionHandler(ErrorCode.USER_NOT_FOUND));

        if (file == null || file.isEmpty()) {

          //set 설정 추가필요
        }

        //이미지 덮어 씌기 위한 옛 이미지 추출
        String oldImageKey = user.getProfileImgKey();
        String oldKey = extractKeyFromUrl(oldImageKey);

        String ext = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf(".")); //원본 파일 마지막 이름 뽑아내기
        String key = "profiles/user_" + userId + "_" + UUID.randomUUID() + ext;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName) //저장 버킷 이름
                .key(key) // 저장 위치 경로
                .acl("public-read") //공개 읽기 권한
                .contentType(file.getContentType())
                .build();

        //기본 프로필 외에 덮어씌기
        if (oldKey != null && !oldKey.contains("기본%20프로필.png")) {
            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(oldKey));
        }

       PutObjectResponse response = s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        if (response.sdkHttpResponse().isSuccessful()) {
            System.out.println("업로드 성공! ETag: " + response.eTag());
        } else {
            System.out.println(" 업로드 실패! 상태 코드: " + response.sdkHttpResponse().statusCode());
        }
        //디버깅을 위한 코드
    }

    private String extractKeyFromUrl(String url) {
        if (url == null || !url.contains(".com/")) return null;
        return url.substring(url.indexOf(".com/") + 5); // ".com/" 이후부터 끝까지
    }


    public String generatePresignedUrl(String fileKey) {
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
