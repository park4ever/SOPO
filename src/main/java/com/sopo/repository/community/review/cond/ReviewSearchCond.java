package com.sopo.repository.community.review.cond;

import java.time.LocalDateTime;

public record ReviewSearchCond(
        Long itemId,
        Long memberId,
        Integer minRating,
        Integer maxRating,
        Boolean hasImage,
        LocalDateTime from,
        LocalDateTime to
) {
    /**
     * 단일 상품 리뷰 조회용
     */
    public static ReviewSearchCond forItem(Long itemId) {
        return new ReviewSearchCond(itemId, null, null, null, null, null, null);
    }

    /**
     * 단일 회원의 리뷰 조회용
     */
    public static ReviewSearchCond forMember(Long memberId) {
        return new ReviewSearchCond(null, memberId, null, null, null, null, null);
    }

    /**
     * 필터 추가 가능한 상품 리뷰 조회용 패턴(향후 확장 대비)
     */
    public static ReviewSearchCond forItemWithFilter(Long itemId, Integer minRating, Integer maxRating,
                                                     Boolean hasImage, LocalDateTime from, LocalDateTime to) {
        return new ReviewSearchCond(itemId, null, minRating, maxRating, hasImage, from, to);
    }
}