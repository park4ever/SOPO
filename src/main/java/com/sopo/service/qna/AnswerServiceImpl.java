package com.sopo.service.qna;

import com.sopo.domain.community.qna.Answer;
import com.sopo.domain.community.qna.QnaStatus;
import com.sopo.domain.community.qna.Question;
import com.sopo.domain.member.Member;
import com.sopo.dto.qna.request.AnswerCreateRequest;
import com.sopo.dto.qna.request.AnswerUpdateRequest;
import com.sopo.dto.qna.response.AnswerResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.community.qna.AnswerRepository;
import com.sopo.repository.community.qna.QuestionRepository;
import com.sopo.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;

    @Override
    public Long create(Long responderId, AnswerCreateRequest request) {
        //질문 조회
        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND));

        //이미 답변이 존재하는지 체크
        if (answerRepository.existsByQuestionId(question.getId())) {
            throw new BusinessException(ErrorCode.QUESTION_ALREADY_ANSWERED);
        }

        //질문 상태 검증
        if (question.getStatus() == QnaStatus.CLOSED) {
            throw new BusinessException(ErrorCode.QUESTION_UPDATE_NOT_ALLOWED);
        }

        //답변자 조회
        Member responder = memberRepository.findById(responderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        //권한 검증
        Long sellerId = question.getItem().getSeller().getId();

        if (!responderId.equals(sellerId)) {
            throw new BusinessException(ErrorCode.QNA_FORBIDDEN_ACCESS);
        }

        Answer answer = Answer.create(question, responder, request.content());
        answerRepository.save(answer);
        //Question 상태 갱신
        question.markAnswered();
        return answer.getId();
    }

    @Override
    public void update(Long responderId, Long answerId, AnswerUpdateRequest request) {
        //답변 조회
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        //소유자 검증
        if (!responderId.equals(answer.getResponder().getId())) {
            throw new BusinessException(ErrorCode.QNA_FORBIDDEN_ACCESS);
        }

        //질문 상태가 CLOSED면 수정 불가
        Question question = answer.getQuestion();
        if (question.getStatus() == QnaStatus.CLOSED) {
            throw new BusinessException(ErrorCode.QUESTION_UPDATE_NOT_ALLOWED);
        }

        //내용 수정
        answer.changeContent(request.content());
    }

    @Override
    @Transactional(readOnly = true)
    public AnswerResponse getByQuestion(Long questionId) {
        Answer answer = answerRepository.findByQuestionId(questionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANSWER_NOT_FOUND));

        return toResponse(answer);
    }

    private AnswerResponse toResponse(Answer a) {
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