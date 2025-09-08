package com.meokplaylist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchUserDto {
    private String nickname;
    private String introduction;
    private String profileImgUrl;
}
