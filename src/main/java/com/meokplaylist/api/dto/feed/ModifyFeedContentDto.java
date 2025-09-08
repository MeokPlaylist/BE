package com.meokplaylist.api.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModifyFeedContentDto {

    private Long feedId;
    private String content;

}
