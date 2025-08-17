package com.meokplaylist.domain.service;

import com.meokplaylist.domain.repository.UsersRepository;
import com.meokplaylist.exception.BizExceptionHandler;
import com.meokplaylist.exception.ErrorCode;
import com.meokplaylist.infra.Users;
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
    private static final String BASE_PROFILE_FMG="https://kr.object.ncloudstorage.com/meokplaylist/%EA%B8%B0%EB%B3%B8%20%ED%94%84%EB%A1%9C%ED%95%84.png";

    private final S3Client s3Client;
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
        String oldImageUrl = user.getProfileImgUrl();
        String oldKey = extractKeyFromUrl(oldImageUrl);

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
}
