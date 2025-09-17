package com.meokplaylist.api.dto.place;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CallInRoadMapDto {

    private String name;
    private String addressName;
    private String roadAddressName;
    private String phone;
    private String kakaoCategoryName;
    private String photoImgUrl;
    private LocalDateTime dateTime;
    //+애견 동반가능인지
}
