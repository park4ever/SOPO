package com.sopo.dto.post.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostUpdateRequest(
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 1000) String content,
        @NotNull Long version
) {}