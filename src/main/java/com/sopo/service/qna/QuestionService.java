package com.sopo.service.qna;

import com.sopo.dto.qna.request.QuestionCreateRequest;
import com.sopo.dto.qna.request.QuestionUpdateRequest;
import com.sopo.dto.qna.response.QuestionDetailResponse;
import com.sopo.dto.qna.response.QuestionSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionService {

    Long create(Long memberId, QuestionCreateRequest request);

    void update(Long memberId, Long questionId, QuestionUpdateRequest request);

    QuestionDetailResponse get(Long questionId, Long memberId);

    /** 상품 상세 페이지용 QnA 목록
     * TODO : 로그인 사용자/판매자에 따라 비공개 노출 정책 분기 */
    Page<QuestionSummaryResponse> getByItem(Long itemId, Pageable pageable);

    /** 마이페이지용 내가 작성한 QnA 목록 */
    Page<QuestionSummaryResponse> getByMember(Long memberId, Pageable pageable);
}