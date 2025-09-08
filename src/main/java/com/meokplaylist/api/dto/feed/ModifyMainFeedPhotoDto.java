package com.meokplaylist.api.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ModifyMainFeedPhotoDto {
    private Long feedId;

    private int newMainFeedPhotoSequence;

    private int oldMainFeedPhotoSequence;

}
