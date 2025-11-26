package com.sopo.dto.qna.response;

import com.sopo.domain.community.qna.QnaStatus;

import java.time.LocalDateTime;

public record QuestionDetailResponse(
        Long id,
        Long itemId,
        String itemName,
        Long askerId,
        String askerName,
        String title,
        String content,
        boolean isPrivate,
        QnaStatus status,
        LocalDateTime createdDate,
        LocalDateTime lastModifiedDate,
        LocalDateTime answeredAt,
        Long version,
        AnswerResponse answer
) {}