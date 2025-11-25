package com.sopo.controller.api.review;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.review.request.ReviewCreateRequest;
import com.sopo.dto.review.request.ReviewUpdateRequest;
import com.sopo.dto.review.response.ReviewResponse;
import com.sopo.security.session.MemberSession;
import com.sopo.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewApiController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<Long> create(@LoginMember MemberSession session,
                                       @Valid @RequestBody ReviewCreateRequest request) {
        Long id = reviewService.create(session.id(), request);
        return ResponseEntity.created(URI.create("/api/reviews/" + id)).body(id);
    }

    @PatchMapping("/reviews/{id}")
    public ResponseEntity<Void> update(@LoginMember MemberSession session,
                                       @PathVariable("id") Long reviewId,
                                       @Valid @RequestBody ReviewUpdateRequest request) {
        reviewService.update(session.id(), reviewId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> delete(@LoginMember MemberSession session,
                                       @PathVariable("id") Long reviewId) {
        reviewService.delete(session.id(), reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reviews{id}")
    public ReviewResponse getById(@PathVariable("id") Long reviewId) {
        return reviewService.getById(reviewId);
    }

    @GetMapping("/items/{itemId}/reviews")
    public Page<ReviewResponse> getByItem(@PathVariable("itemId") Long itemId, Pageable pageable) {
        return reviewService.getByItem(itemId, pageable);
    }

    @GetMapping("/members/me/reviews")
    public Page<ReviewResponse> getMyReviews(@LoginMember MemberSession session,
                                             Pageable pageable) {
        return reviewService.getByMember(session.id(), pageable);
    }

    @GetMapping("/order-items/{orderItemId}/reviews/exists")
    public Map<String, Boolean> existsForOrderItem(@LoginMember MemberSession session,
                                                   @PathVariable("orderItemId") Long orderItemId) {
        boolean exists = reviewService.existsForOrderItem(session.id(), orderItemId);
        return Map.of("exists", exists);
    }
}