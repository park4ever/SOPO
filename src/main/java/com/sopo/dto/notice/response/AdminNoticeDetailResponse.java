package com.sopo.dto.notice.response;

import java.time.LocalDateTime;

public record AdminNoticeDetailResponse(
        Long id,
        String title,
        String content,
        boolean pinned,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate,
        Long version
) {}