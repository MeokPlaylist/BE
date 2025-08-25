package com.meokplaylist.api.dto.feed;

import org.springframework.data.domain.Slice;

import java.util.List;

public record SlicedResponse<T>(
        List<T> content,
        int page,   // 요청한 page 번호(보통 0부터 증가)
        int size,
        boolean first,
        boolean last,
        boolean empty,
        boolean hasNext
) {
    public static <T> SlicedResponse<T> of(Slice<T> s) {
        return new SlicedResponse<>(
                s.getContent(),
                s.getNumber(),
                s.getSize(),
                s.isFirst(),
                s.isLast(),
                s.isEmpty(),
                s.hasNext()
        );
    }
}

