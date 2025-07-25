package com.meokplaylist.api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record UserProfileSetupRequest(
        @NotBlank MultipartFile profileImg,
        @NotBlank String nickname,
        @NotBlank @Email String email,
        String introduction
) {}
