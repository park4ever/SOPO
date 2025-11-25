package com.sopo.controller.view.review;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.review.request.ReviewUpdateRequest;
import com.sopo.dto.review.response.ReviewImageResponse;
import com.sopo.dto.review.response.ReviewResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.security.session.MemberSession;
import com.sopo.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/my")
    public String listMyReviews(@LoginMember MemberSession session,
                                Pageable pageable, Model model) {
        Page<ReviewResponse> page = reviewService.getByMember(session.id(), pageable);
        model.addAttribute("page", page);
        return "review/my-list";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@LoginMember MemberSession session,
                           @PathVariable("id") Long reviewId, Model model) {
        ReviewResponse review = reviewService.getById(reviewId);

        if (!Objects.equals(review.memberId(), session.id())) {
            throw new BusinessException(ErrorCode.REVIEW_FORBIDDEN_ACCESS);
        }

        ReviewUpdateRequest form = new ReviewUpdateRequest(
                review.content(),
                review.rating(),
                review.images().stream()
                                .map(ReviewImageResponse::imageUrl)
                                        .toList(),
                review.version()
        );

        model.addAttribute("review", review);
        model.addAttribute("form", form);
        return "review/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@LoginMember MemberSession session,
                         @PathVariable("id") Long reviewId,
                         @Valid @ModelAttribute("form") ReviewUpdateRequest form,
                         RedirectAttributes ra) {
        reviewService.update(session.id(), reviewId, form);
        ra.addFlashAttribute("message", "리뷰가 수정되었습니다.");
        return "redirect:/reviews/my";
    }

    @PostMapping("/{id}/delete")
    public String delete(@LoginMember MemberSession session,
                         @PathVariable("id") Long reviewId,
                         RedirectAttributes ra) {
        reviewService.delete(session.id(), reviewId);
        ra.addFlashAttribute("message", "리뷰가 삭제되었습니다.");
        return "redirect:/reviews/my";
    }
}