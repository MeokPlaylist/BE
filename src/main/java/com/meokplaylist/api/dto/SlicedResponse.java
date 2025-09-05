package com.meokplaylist.api.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public record SlicedResponse<T>(
        List<T> content,
        int size,
        boolean first,
        boolean last,
        boolean empty,
        boolean hasNext
) {
    public static <T> SlicedResponse<T> of(Slice<T> s) {
        return new SlicedResponse<>(
                s.getContent(),
                s.getSize(),
                s.isFirst(),
                s.isLast(),
                s.isEmpty(),
                s.hasNext()
        );
    }
}

