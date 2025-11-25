package com.sopo.service.review;

import com.sopo.dto.review.request.ReviewCreateRequest;
import com.sopo.dto.review.request.ReviewUpdateRequest;
import com.sopo.dto.review.response.ReviewResponse;
import com.sopo.dto.review.response.ReviewSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    Long create(Long memberId, ReviewCreateRequest request);

    void update(Long memberId, Long reviewId, ReviewUpdateRequest request);

    void delete(Long memberId, Long reviewId);

    /** 리뷰 단건 조회 - 관리자/마이페이지/디테일 화면 공용 */
    ReviewResponse getById(Long reviewId);

    /** 상품 상세용 리뷰 목록 */
    Page<ReviewResponse> getByItem(Long itemId, Pageable pageable);

    /** 마이페이지용 내 리뷰 목록 */
    Page<ReviewResponse> getByMember(Long memberId, Pageable pageable);

    /** 상품 리뷰 요약 지표 */
    ReviewSummaryResponse getSummaryForItem(Long itemId);

    /** 특정 주문 항목에 대해 내가 이미 리뷰를 작성했는지 여부 */
    boolean existsForOrderItem(Long memberId, Long orderItemId);
}