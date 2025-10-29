package com.sopo.controller.api.coupon;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.coupon.request.PreviewMemberCouponRequest;
import com.sopo.dto.coupon.request.UseMemberCouponRequest;
import com.sopo.dto.coupon.response.MemberCouponRowResponse;
import com.sopo.repository.memberCoupon.cond.MemberCouponQueryCond;
import com.sopo.security.session.MemberSession;
import com.sopo.service.coupon.MemberCouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member-coupons")
public class MemberCouponApiController {

    private final MemberCouponService memberCouponService;

    @GetMapping("/my")
    public Page<MemberCouponRowResponse> myCoupons(@LoginMember MemberSession session,
                                                   MemberCouponQueryCond cond, Pageable pageable) {
        return memberCouponService.searchMy(session.id(), cond, pageable, LocalDateTime.now());
    }

    @PostMapping("/{id}/preview")
    public BigDecimal preview(@LoginMember MemberSession session,
                              @PathVariable("id") Long memberCouponId,
                              @RequestBody @Valid PreviewMemberCouponRequest request) {
        return memberCouponService.preview(memberCouponId, session.id(), request, LocalDateTime.now());
    }

    @PostMapping("/{id}/use")
    public ResponseEntity<Void> use(@LoginMember MemberSession session,
                                    @PathVariable("id") Long memberCouponId,
                                    @RequestBody @Valid UseMemberCouponRequest request) {
        //memberId는 서버에서 강제 주입(클라이언트 제공값 무시)
        var effective = new UseMemberCouponRequest(
                memberCouponId,
                session.id(),
                request.orderId(),
                request.orderPrice()
        );
        memberCouponService.use(effective, LocalDateTime.now());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@LoginMember MemberSession session,
                                       @PathVariable("id") Long memberCouponId) {
        memberCouponService.cancelUse(memberCouponId, session.id(), LocalDateTime.now());
        return ResponseEntity.noContent().build();
    }
}