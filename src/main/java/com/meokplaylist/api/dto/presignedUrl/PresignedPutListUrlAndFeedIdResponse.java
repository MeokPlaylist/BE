package com.meokplaylist.api.dto.presignedUrl;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PresignedPutListUrlAndFeedIdResponse {
    private List<String> presignedPutUrls;
    private Long feedId;
}
