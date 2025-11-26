package com.sopo.dto.qna.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AnswerCreateRequest(
        @NotNull Long questionId,
        @NotBlank @Size(max = 1000) String content
) {}