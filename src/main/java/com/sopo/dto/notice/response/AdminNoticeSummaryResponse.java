package com.sopo.dto.notice.response;

import java.time.LocalDateTime;

public record AdminNoticeSummaryResponse(
        Long id,
        String title,
        boolean pinned,
        LocalDateTime createdDate
) {}