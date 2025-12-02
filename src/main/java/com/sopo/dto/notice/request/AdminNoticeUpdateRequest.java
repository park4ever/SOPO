package com.sopo.dto.notice.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminNoticeUpdateRequest(
        @NotNull
        Long version,
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 4000) String content,
        boolean pinned
) {}