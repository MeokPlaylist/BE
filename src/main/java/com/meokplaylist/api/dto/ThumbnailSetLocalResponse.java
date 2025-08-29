package com.meokplaylist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ThumbnailSetLocalResponse {
    Map<String, List<String>> groupedUrlsByLocal;
}
