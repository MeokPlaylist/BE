package com.meokplaylist.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfileSetupResponse {
        private String profilePutPresignedUrl;
}
