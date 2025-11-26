package com.sopo.repository.community.qna;

import com.sopo.domain.community.qna.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepositoryCustom {

    /** 상품 상세 페이지용 공개 QnA 목록 페이징 */
    Page<Question> searchPublicByItem(Long itemId, Pageable pageable);

    /** 마이페이지용 내가 작성한 QnA 목록 페이징 */
    Page<Question> searchByMember(Long memberId, Pageable pageable);
}