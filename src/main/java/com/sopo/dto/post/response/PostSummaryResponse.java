package com.sopo.dto.post.response;

import java.time.LocalDateTime;

public record PostSummaryResponse(
        Long id,
        Long authorId,
        String authorName,
        String title,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {}