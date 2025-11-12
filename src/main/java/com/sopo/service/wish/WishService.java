package com.sopo.service.wish;

import com.sopo.dto.wish.request.WishCreateRequest;
import com.sopo.dto.wish.response.WishResponse;

import java.util.List;

public interface WishService {

    /** 찜 추가(memberId + itemId 유니크) */
    Long add(Long memberId, WishCreateRequest request);

    /** 찜 삭제 (wishId 기준) */
    void remove(Long memberId, Long wishId);

    /** 찜 삭제(itemId 기준 -> 프런트가 wishId 모를 때) */
    void removeByItem(Long memberId, Long itemId);

    /** 내 찜 목록(최신순) */
    List<WishResponse> listMine(Long memberId);

    /** 특정 상품 찜 여부(상세 페이지 하트 상태 표시용) */
    boolean exists(Long memberId, Long itemId);

    /** 특정 상품의 총 찜 수(상세 페이지 카운트 표시용) */
    long countForItem(Long itemId);

    /** 토글 */
    boolean toggle(Long memberId, Long itemId);
}