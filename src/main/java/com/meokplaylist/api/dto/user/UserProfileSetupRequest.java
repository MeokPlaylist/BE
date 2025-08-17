package com.meokplaylist.api.dto.user;

import org.springframework.web.multipart.MultipartFile;

public record UserProfileSetupRequest(
         MultipartFile profileImg
) {}
