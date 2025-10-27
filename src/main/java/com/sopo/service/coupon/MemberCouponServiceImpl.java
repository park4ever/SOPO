package com.sopo.service.coupon;

import com.sopo.domain.coupon.Coupon;
import com.sopo.domain.coupon.MemberCoupon;
import com.sopo.domain.coupon.MemberCouponStatus;
import com.sopo.domain.member.Member;
import com.sopo.domain.order.Order;
import com.sopo.dto.coupon.request.IssueRequest;
import com.sopo.dto.coupon.request.PreviewMemberCouponRequest;
import com.sopo.dto.coupon.request.UseMemberCouponRequest;
import com.sopo.dto.coupon.response.MemberCouponRowResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.coupon.CouponRepository;
import com.sopo.repository.member.MemberRepository;
import com.sopo.repository.memberCoupon.MemberCouponRepository;
import com.sopo.repository.memberCoupon.cond.MemberCouponQueryCond;
import com.sopo.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.sopo.domain.coupon.MemberCouponStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCouponServiceImpl implements MemberCouponService {

    private final MemberCouponRepository memberCouponRepository;
    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    @Override
    public Long issue(IssueRequest request, LocalDateTime now) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        Coupon coupon = couponRepository.findById(request.couponId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
        if (!coupon.isActiveAt(now)) {
            throw new BusinessException(ErrorCode.COUPON_NOT_ACTIVE);
        }

        MemberCoupon memberCoupon = MemberCoupon.issueTo(member, coupon, now);
        return memberCouponRepository.save(memberCoupon).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal preview(Long memberCouponId, Long memberId, PreviewMemberCouponRequest request, LocalDateTime now) {
        return memberCouponRepository.findUsableByIdAndMember(memberCouponId, memberId, now)
                .map(mc -> mc.getCoupon().previewDiscountIfAvailable(request.orderPrice(), now))
                .orElseGet(() -> BigDecimal.ZERO.setScale(2));
    }

    @Override
    public void use(UseMemberCouponRequest request, LocalDateTime now) {
        MemberCoupon memberCoupon = memberCouponRepository
                .findUsableByIdAndMember(request.memberCouponId(), request.memberId(), now)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_COUPON_NOT_USABLE));

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        try {
            memberCoupon.markUsed(order, request.orderPrice(), now);
        } catch (OptimisticLockingFailureException e) {
            throw new BusinessException(ErrorCode.OPTIMISTIC_LOCK_CONFLICT);
        }
    }

    @Override
    public void cancelUse(Long memberCouponId, Long memberId, LocalDateTime now) {
        MemberCoupon memberCoupon = memberCouponRepository.findByIdAndMemberId(memberCouponId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_COUPON_NOT_FOUND));

        memberCoupon.markCanceled(now);
    }

    @Override
    public int expireAll(LocalDateTime now, int batchSize) {
        int changed = 0;
        for (Long id : memberCouponRepository.findIdsToExpire(now, batchSize)) {
            memberCouponRepository.findById(id).ifPresent(mc -> {
                if (mc.getStatus() == ISSUED || mc.getStatus() == CANCELED) {
                    mc.expire(now);
                }
            });
            changed++;
        }
        return changed;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberCouponRowResponse> search(MemberCouponQueryCond cond, Pageable pageable, LocalDateTime now) {
        return memberCouponRepository.search(cond, pageable, now)
                .map(this::toRowResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MemberCouponRowResponse> searchMy(Long memberId, MemberCouponQueryCond cond, Pageable pageable, LocalDateTime now) {
        return memberCouponRepository.searchMyCoupons(memberId, cond, pageable, now)
                .map(this::toRowResponse);
    }

    private MemberCouponRowResponse toRowResponse(MemberCoupon mc) {
        Coupon c = mc.getCoupon();
        return new MemberCouponRowResponse(
                mc.getId(),
                mc.getStatus().name(),
                mc.getIssuedAt(),
                mc.getUsedAt(),
                c.getName(),
                c.getMinOrderPrice(),
                c.getValidFrom(),
                c.getValidUntil()
        );
    }
}