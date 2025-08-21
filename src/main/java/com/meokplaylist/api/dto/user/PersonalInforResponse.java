package com.meokplaylist.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@Builder
public class PersonalInforResponse {
        private String name;
        private String email;
        private LocalDate birthDay;
        private OffsetDateTime createdAt;
        private Boolean OauthUser;
}
