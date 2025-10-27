package com.sopo.service.coupon;

import com.sopo.domain.coupon.Coupon;
import com.sopo.dto.coupon.request.CreateFixedCouponRequest;
import com.sopo.dto.coupon.request.CreateRateCouponRequest;
import com.sopo.dto.coupon.request.PreviewCouponRequest;
import com.sopo.dto.coupon.response.CouponDetailResponse;
import com.sopo.dto.coupon.response.CouponSummaryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.coupon.CouponRepository;
import com.sopo.repository.coupon.cond.CouponQueryCond;
import com.sopo.security.aop.AdminOnly;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@AdminOnly
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public Long createFixed(CreateFixedCouponRequest request) {
        Coupon coupon = Coupon.createFixed(
                request.name(),
                request.fixedAmount(),
                request.minOrderPrice(),
                request.validFrom(),
                request.validUntil()
        );

        return couponRepository.save(coupon).getId();
    }

    @Override
    @Transactional
    public Long createRate(CreateRateCouponRequest request) {
        Coupon coupon = Coupon.createRate(
                request.name(),
                request.percentage(),
                request.maxDiscountAmount(),
                request.minOrderPrice(),
                request.validFrom(),
                request.validUntil()
        );

        return couponRepository.save(coupon).getId();
    }

    @Override
    public BigDecimal preview(Long couponId, PreviewCouponRequest request, LocalDateTime now) {
        return couponRepository.findActiveById(couponId, now)
                .map(c -> c.previewDiscountIfAvailable(request.orderPrice(), now))
                .orElseGet(() -> BigDecimal.ZERO.setScale(2));
    }

    @Override
    public CouponDetailResponse get(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        boolean active = coupon.isActiveAt(LocalDateTime.now());
        return toDetailResponse(coupon, active);
    }

    @Override
    public Page<CouponSummaryResponse> search(CouponQueryCond cond, Pageable pageable, LocalDateTime now) {
        return couponRepository.search(cond, pageable, now)
                .map(c -> toSummaryResponse(c, c.isActiveAt(now)));
    }

    @Override
    @Transactional
    public void softDelete(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        coupon.delete(); //@SQLDelete + is_deleted 플래그
    }

    private CouponSummaryResponse toSummaryResponse(Coupon coupon, boolean active) {
        return new CouponSummaryResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountType().name(),
                coupon.getFixedAmount(),
                coupon.getPercentage(),
                coupon.getMaxDiscountAmount(),
                coupon.getMinOrderPrice(),
                coupon.getValidFrom(),
                coupon.getValidUntil(),
                active
        );
    }

    private CouponDetailResponse toDetailResponse(Coupon coupon, boolean active) {
        return new CouponDetailResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountType().name(),
                coupon.getFixedAmount(),
                coupon.getPercentage(),
                coupon.getMaxDiscountAmount(),
                coupon.getMinOrderPrice(),
                coupon.getValidFrom(),
                coupon.getValidUntil(),
                coupon.isDeleted(),
                active
        );
    }
}