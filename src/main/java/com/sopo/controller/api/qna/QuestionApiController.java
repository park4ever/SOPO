package com.sopo.controller.api.qna;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.qna.request.QuestionCreateRequest;
import com.sopo.dto.qna.request.QuestionUpdateRequest;
import com.sopo.dto.qna.response.QuestionDetailResponse;
import com.sopo.dto.qna.response.QuestionSummaryResponse;
import com.sopo.security.session.MemberSession;
import com.sopo.service.qna.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuestionApiController {

    private final QuestionService questionService;

    @PostMapping("/questions")
    public ResponseEntity<Long> create(@LoginMember MemberSession session,
                                       @Valid @RequestBody QuestionCreateRequest request) {
        Long id = questionService.create(session.id(), request);
        return ResponseEntity.created(URI.create("/api/questions/" + id)).body(id);
    }

    @PatchMapping("/questions/{id}")
    public ResponseEntity<Void> update(@LoginMember MemberSession session,
                                       @PathVariable("id") Long questionId,
                                       @Valid @RequestBody QuestionUpdateRequest request) {
        questionService.update(session.id(), questionId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/questions/{id}")
    public QuestionDetailResponse get(@LoginMember MemberSession session,
                                      @PathVariable("id") Long questionId) {
        Long memberId = (session != null) ? session.id() : null;
        return questionService.get(questionId, memberId);
    }

    @GetMapping("/items/{itemId}/questions")
    public Page<QuestionSummaryResponse> getByItem(@PathVariable("itemId") Long itemId,
                                                   Pageable pageable) {
        return questionService.getByItem(itemId, pageable);
    }

    @GetMapping("/members/me/questions")
    public Page<QuestionSummaryResponse> getMyQuestions(@LoginMember MemberSession session,
                                                        Pageable pageable) {
        return questionService.getByMember(session.id(), pageable);
    }
}