package com.sopo.dto.notice.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminNoticeCreateRequest(
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 4000) String content,
        boolean pinned
) {}