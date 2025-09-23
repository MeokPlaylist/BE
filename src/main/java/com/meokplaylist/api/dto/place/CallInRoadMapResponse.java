package com.meokplaylist.api.dto.place;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CallInRoadMapResponse {
    private List<CallInRoadMapDto> callInRoadMapDtoList;
}
