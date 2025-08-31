package com.meokplaylist.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CallInRoadMapResponse {
    private List<CallInRoadMapDto> callInRoadMapDtoList;
}
