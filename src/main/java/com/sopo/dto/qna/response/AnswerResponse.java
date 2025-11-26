package com.sopo.dto.qna.response;

import java.time.LocalDateTime;

public record AnswerResponse(
        Long id,
        Long questionId,
        Long responderId,
        String responderName,
        String content,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate
) {}