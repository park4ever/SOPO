package com.sopo.dto.qna.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuestionUpdateRequest(
        @NotBlank @Size(max = 1000) String content,
        @Size(max = 100) String title,
        @NotNull Boolean isPrivate,
        @NotNull Long version
) {}