package com.sopo.repository.coupon.cond;

import com.sopo.domain.coupon.DiscountType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class CouponQueryCond {

    /** 쿠폰명 검색(부분 일치, 대소문자 무시) */
    private final String nameContains;

    /** 타입 필터: FIXED / RATE (선택) */
    private final DiscountType discountType;

    /** 활성 상태만(= now가 [validFrom, validUntil]에 포함) */
    @Builder.Default
    private final boolean activeOnly = false;

    /** 유효기간과의 겹침 검색용 범위(포함) — activeOnly=false일 때 유효 */
    private final LocalDateTime periodFrom;  // 조회기간 시작
    private final LocalDateTime periodUntil; // 조회기간 끝

    /** 최소 주문 금액 범위(옵션) */
    private final BigDecimal minOrderPriceMin; // >=
    private final BigDecimal minOrderPriceMax; // <=

    /** 정률 cap 유무 필터 (RATE일 때 의미) */
    private final Boolean hasMaxDiscountCap; // null=무시, true=있는 것만, false=없는 것만

    /** 정렬 스펙(화이트리스트). 없으면 기본: validUntil DESC, id DESC */
    @Builder.Default
    private final List<CouponSortSpec> sorts = Collections.emptyList();
}