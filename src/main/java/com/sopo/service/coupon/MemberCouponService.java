package com.sopo.service.coupon;

import com.sopo.dto.coupon.request.IssueRequest;
import com.sopo.dto.coupon.request.PreviewMemberCouponRequest;
import com.sopo.dto.coupon.request.UseMemberCouponRequest;
import com.sopo.dto.coupon.response.MemberCouponRowResponse;
import com.sopo.repository.memberCoupon.cond.MemberCouponQueryCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface MemberCouponService {

    /** 관리자/이벤트/자가발급 공용 엔드포인트 */
    Long issue(IssueRequest request, LocalDateTime now);

    /** 미리보기 : 사용 가능 조건이면 할인액, 아니면 0 */
    BigDecimal preview(Long memberCouponId, Long memberId, PreviewMemberCouponRequest request, LocalDateTime now);

    /** 주문에 사용(낙관 락 기반 중복 방지) */
    void use(UseMemberCouponRequest request, LocalDateTime now);

    /** 사용 취소(주문 취소 등) */
    void cancelUse(Long memberCouponId, Long memberId, LocalDateTime now);

    /** 만료 배치(청크 처리) -> 변경 수 반환 */
    int expireAll(LocalDateTime now, int batchSize);

    /** 목록(관리자/사용자 공용 Cond) */
    Page<MemberCouponRowResponse> search(MemberCouponQueryCond cond, Pageable pageable, LocalDateTime now);

    /** 내 쿠폰 목록(편의 메서드) */
    Page<MemberCouponRowResponse> searchMy(Long memberId, MemberCouponQueryCond cond, Pageable pageable, LocalDateTime now);
}