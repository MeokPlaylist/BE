package com.meokplaylist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
@AllArgsConstructor
public class UserSearchResponse {
    private Slice<UserSearchDto> userSearchslice;
}
