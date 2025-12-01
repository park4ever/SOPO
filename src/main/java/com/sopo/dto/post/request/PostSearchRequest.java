package com.sopo.dto.post.request;

import com.sopo.repository.community.post.cond.PostSearchCond;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record PostSearchRequest(
        String keyword,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime from,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime to
) {
    public PostSearchCond toCond() {
        return new PostSearchCond(null, keyword, from, to);
    }
}