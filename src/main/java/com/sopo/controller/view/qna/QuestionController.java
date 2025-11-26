package com.sopo.controller.view.qna;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.qna.request.QuestionCreateRequest;
import com.sopo.dto.qna.request.QuestionUpdateRequest;
import com.sopo.dto.qna.response.QuestionDetailResponse;
import com.sopo.dto.qna.response.QuestionSummaryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.security.session.MemberSession;
import com.sopo.service.qna.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static java.lang.Boolean.*;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/mypage/questions")
    public String myQuestions(@LoginMember MemberSession session,
                              Pageable pageable, Model model) {
        Page<QuestionSummaryResponse> page = questionService.getByMember(session.id(), pageable);
        model.addAttribute("page", page);
        return "qna/my-questions";
    }

    @GetMapping("/items/{itemId}/questions/new")
    public String createForm(@LoginMember MemberSession session,
                             @PathVariable("itemId") Long itemId, Model model) {
        QuestionCreateRequest form =
                new QuestionCreateRequest(itemId, null, null, FALSE);
        model.addAttribute("itemId", itemId);
        model.addAttribute("form", form);
        return "qna/new";
    }

    @PostMapping("/items/{itemId}/questions")
    public String create(@LoginMember MemberSession session,
                         @PathVariable("itemId") Long itemId,
                         @Valid @ModelAttribute("form") QuestionCreateRequest form) {
        //path의 itemId를 신뢰하고 DTO를 재조립
        QuestionCreateRequest request =
                new QuestionCreateRequest(itemId, form.title(), form.content(), form.isPrivate());

        questionService.create(session.id(), request);
        return "redirect:/items/" + itemId; //TODO : 실제 상품 상세 페이지 경로에 맞게 수정
    }

    @GetMapping("/questions/{id}/edit")
    public String editForm(@LoginMember MemberSession session,
                           @PathVariable("id") Long questionId, Model model) {
        QuestionDetailResponse question = questionService.get(questionId, session.id());

        //작성자 검증
        if (!Objects.equals(question.askerId(), session.id())) {
            throw new BusinessException(ErrorCode.QNA_FORBIDDEN_ACCESS);
        }

        QuestionUpdateRequest form = new QuestionUpdateRequest(
                question.content(),
                question.title(),
                question.isPrivate(),
                question.version()
        );

        model.addAttribute("question", question);
        model.addAttribute("form", form);
        return "qna/edit";
    }

    @PostMapping("/questions/{id}/edit")
    public String edit(@LoginMember MemberSession session,
                       @PathVariable("id") Long questionId,
                       @Valid @ModelAttribute("form") QuestionUpdateRequest form) {
        questionService.update(session.id(), questionId, form);

        return "redirect:/mypage/questions";
    }
}