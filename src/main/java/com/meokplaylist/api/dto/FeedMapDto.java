package com.meokplaylist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FeedMapDto {
    private List<String> photoUrls;
    private Long likeNum;

}
