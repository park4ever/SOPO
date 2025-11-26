package com.sopo.service.qna;

import com.sopo.dto.qna.request.AnswerCreateRequest;
import com.sopo.dto.qna.request.AnswerUpdateRequest;
import com.sopo.dto.qna.response.AnswerResponse;

public interface AnswerService {

    Long create(Long responderId, AnswerCreateRequest request);

    void update(Long responderId, Long answerId, AnswerUpdateRequest request);

    AnswerResponse getByQuestion(Long questionId);
}