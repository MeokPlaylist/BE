package com.meokplaylist.api.dto.user;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public record UserProfileSetupRequest(
        String fileName,
        LocalDateTime dayAndTime
) {}
