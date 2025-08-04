package com.meokplaylist.api.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserMe {
    private Long id;
    private KakaoAccount kakao_account;

    @Getter @Setter
    public static class KakaoAccount {
        private String email;
        private Profile profile;
    }

    @Getter @Setter
    public static class Profile{
        private String Nickname;
        private String profile_image_url;
    }
}
