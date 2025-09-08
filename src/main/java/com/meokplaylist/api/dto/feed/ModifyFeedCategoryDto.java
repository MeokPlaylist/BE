package com.meokplaylist.api.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
public class ModifyFeedCategoryDto {
    private Long feedId;
    private List<String> categories;
    private List<String> regions;
}
