package com.sopo.dto.post.response;

import java.time.LocalDateTime;

public record PostDetailResponse(
        Long id,
        Long authorId,
        String authorName,
        String title,
        String content,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate,
        Long version
) {}