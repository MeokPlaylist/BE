package com.meokplaylist.api.dto.socialInteraction;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WriteFeedCommentsDto {
    private Long feedId;
    private String nickname;
    private String content;
}
