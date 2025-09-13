package com.meokplaylist.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class CallInRoadMapDto {

    private String name;
    private String addressName;
    private String roadAddressName;
    private String phone;
    private String kakaoCategoryName;
    private String photoImgUrl;
    //+애견 동반가능인지
}
