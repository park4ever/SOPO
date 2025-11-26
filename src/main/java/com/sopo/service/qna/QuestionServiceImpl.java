package com.sopo.service.qna;

import com.sopo.domain.community.qna.Answer;
import com.sopo.domain.community.qna.QnaStatus;
import com.sopo.domain.community.qna.Question;
import com.sopo.domain.item.Item;
import com.sopo.domain.member.Member;
import com.sopo.dto.qna.request.QuestionCreateRequest;
import com.sopo.dto.qna.request.QuestionUpdateRequest;
import com.sopo.dto.qna.response.AnswerResponse;
import com.sopo.dto.qna.response.QuestionDetailResponse;
import com.sopo.dto.qna.response.QuestionSummaryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.community.qna.AnswerRepository;
import com.sopo.repository.community.qna.QuestionRepository;
import com.sopo.repository.item.ItemRepository;
import com.sopo.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public Long create(Long memberId, QuestionCreateRequest request) {
        //회원 검증
        Member asker = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        //상품 검증
        Item item = itemRepository.findById(request.itemId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));

        if (item.isDeleted()) {
            throw new BusinessException(ErrorCode.ITEM_DELETED);
        }

        Question question = Question.create(
                asker,
                item,
                request.title(),
                request.content(),
                request.isPrivate() != null && request.isPrivate()
        );

        questionRepository.save(question);
        return question.getId();
    }

    @Override
    @Transactional
    public void update(Long memberId, Long questionId, QuestionUpdateRequest request) {
        //소유자 검증 + 존재 확인
        Question question = questionRepository.findByIdAndAskerId(questionId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        //상태 검증
        if (question.getStatus() != QnaStatus.OPEN) {
            throw new BusinessException(ErrorCode.QUESTION_UPDATE_NOT_ALLOWED);
        }

        //내용 수정
        question.updateContent(request.title(), request.content());
        //TODO : isPrivate 변경 여부는 추후 정책 확정 시 도입(지금은 엔티티 메서드 시그니처가 명확하지 않음.)
    }

    @Override
    public QuestionDetailResponse get(Long questionId, Long memberId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        //접근 제어 : 비공개 문의는 작성자나 판매자만 열람 가능
        if (question.isPrivate()) {
            if (memberId == null) {
                throw new BusinessException(ErrorCode.QNA_FORBIDDEN_ACCESS);
            }

            Long askerId = question.getAsker().getId();
            Long sellerId = question.getItem().getSeller().getId();

            if (!memberId.equals(askerId) && !memberId.equals(sellerId)) {
                throw new BusinessException(ErrorCode.QNA_FORBIDDEN_ACCESS);
            }
        }

        //답변 로딩
        Optional<Answer> answerOpt = answerRepository.findByQuestionId(question.getId());

        AnswerResponse answerResponse = answerOpt
                .map(this::toAnswerResponse)
                .orElse(null);

        return toDetailResponse(question, answerResponse);
    }

    @Override
    public Page<QuestionSummaryResponse> getByItem(Long itemId, Pageable pageable) {
        Page<Question> page = questionRepository.searchPublicByItem(itemId, pageable);
        return page.map(this::toSummaryResponse);
    }

    @Override
    public Page<QuestionSummaryResponse> getByMember(Long memberId, Pageable pageable) {
        Page<Question> page = questionRepository.searchByMember(memberId, pageable);
        return page.map(this::toSummaryResponse);
    }

    private QuestionSummaryResponse toSummaryResponse(Question q) {
        return new QuestionSummaryResponse(
                q.getId(),
                q.getItem().getId(),
                q.getItem().getName(),
                q.getAsker().getId(),
                q.getAsker().getName(),
                q.getTitle(),
                q.getContent(),
                q.isPrivate(),
                q.getStatus(),
                q.getStatus() == QnaStatus.ANSWERED,
                q.getCreatedDate(),
                q.getAnsweredAt()
        );
    }

    private QuestionDetailResponse toDetailResponse(Question q, AnswerResponse answer) {
        return new QuestionDetailResponse(
                q.getId(),
                q.getItem().getId(),
                q.getItem().getName(),
                q.getAsker().getId(),
                q.getAsker().getName(),
                q.getTitle(),
                q.getContent(),
                q.isPrivate(),
                q.getStatus(),
                q.getCreatedDate(),
                q.getLastModifiedDate(),
                q.getAnsweredAt(),
                answer
        );
    }

    private AnswerResponse toAnswerResponse(Answer a) {
        return new AnswerResponse(
                a.getId(),
                a.getQuestion().getId(),
                a.getResponder().getId(),
                a.getResponder().getName(),
                a.getContent(),
                a.getCreatedDate(),
                a.getLastModifiedDate()
        );
    }
}