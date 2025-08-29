package com.meokplaylist.api.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FeedMapDto {
    private List<String> photoUrls;
    private long likeCoount;
    private long commetCount;

}
