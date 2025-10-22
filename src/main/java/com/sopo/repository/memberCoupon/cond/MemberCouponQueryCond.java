package com.sopo.repository.memberCoupon.cond;

import com.sopo.domain.coupon.MemberCouponStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Getter
@Builder(toBuilder = true)
public class MemberCouponQueryCond {

    /** 조회 주체 */
    private final Long memberId;   // 사용자 관점 목록
    private final Long couponId;   // 특정 쿠폰의 소지건 찾기(선택)

    /** 상태 필터(다중) — ISSUED/USED/CANCELED/EXPIRED */
    @Builder.Default
    private final Set<MemberCouponStatus> statuses = Collections.emptySet();

    /** 활성 쿠폰만 (소지건 상태와 별개로 정책 기간 기준) */
    @Builder.Default
    private final boolean activePolicyOnly = false;

    /** 소지/사용/취소 일자 범위(필터는 화면 요구에 맞춰 선택 적용) */
    private final LocalDateTime issuedFrom;
    private final LocalDateTime issuedUntil;
    private final LocalDateTime usedFrom;
    private final LocalDateTime usedUntil;

    /** 만료 배치용: 정책 만료 기준(now 이전)만 추출할지 */
    @Builder.Default
    private final boolean expireTargetOnly = false;

    /** 정렬 스펙 — 기본 issuedAt DESC, id DESC */
    @Builder.Default
    private final List<MemberCouponSortSpec> sorts = Collections.emptyList();
}