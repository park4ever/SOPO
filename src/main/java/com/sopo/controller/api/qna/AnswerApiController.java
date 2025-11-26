package com.sopo.controller.api.qna;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.qna.request.AnswerCreateRequest;
import com.sopo.dto.qna.request.AnswerUpdateRequest;
import com.sopo.dto.qna.response.AnswerResponse;
import com.sopo.security.session.MemberSession;
import com.sopo.service.qna.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AnswerApiController {

    private final AnswerService answerService;

    @PostMapping("/answers")
    public ResponseEntity<Long> create(@LoginMember MemberSession session,
                                       @Valid @RequestBody AnswerCreateRequest request) {
        Long id = answerService.create(session.id(), request);
        return ResponseEntity.created(URI.create("/api/answers/" + id)).body(id);
    }

    @PatchMapping("/answers/{id}")
    public ResponseEntity<Void> update(@LoginMember MemberSession session,
                                       @PathVariable("id") Long answerId,
                                       @Valid @RequestBody AnswerUpdateRequest request) {
        answerService.update(session.id(), answerId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/questions/{questionId}/answer")
    public AnswerResponse getByQuestion(@PathVariable("questionId") Long questionId) {
        return answerService.getByQuestion(questionId);
    }
}