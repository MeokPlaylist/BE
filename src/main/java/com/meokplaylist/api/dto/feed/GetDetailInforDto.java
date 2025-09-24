package com.meokplaylist.api.dto.feed;

import com.meokplaylist.infra.feed.FeedCategory;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class GetDetailInforDto {

     private String nickName;
     private String content;
     private List<String> hashTag;
     private OffsetDateTime createdAt;
     private List<String> feedPhotoUrl;
     private List<FeedCategory> feedCategories;
     private Boolean feedLike;
     private long likeCount;
     private long commentCount;

}
