package com.meokplaylist.api.dto.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class MyProfileResponse {
    String profileUrl;
    String userNickname;
    String userIntro;
}
