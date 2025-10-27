package com.sopo.service.coupon;

import com.sopo.dto.coupon.request.CreateFixedCouponRequest;
import com.sopo.dto.coupon.request.CreateRateCouponRequest;
import com.sopo.dto.coupon.request.PreviewCouponRequest;
import com.sopo.dto.coupon.response.CouponDetailResponse;
import com.sopo.dto.coupon.response.CouponSummaryResponse;
import com.sopo.repository.coupon.cond.CouponQueryCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface CouponService {

    Long createFixed(CreateFixedCouponRequest request);

    Long createRate(CreateRateCouponRequest request);

    /** 활성/최소 주문 충족 시 할인액, 아니면 0 (예외 대신 0 반환) */
    BigDecimal preview(Long couponId, PreviewCouponRequest request, LocalDateTime now);

    CouponDetailResponse get(Long couponId);

    Page<CouponSummaryResponse> search(CouponQueryCond cond, Pageable pageable, LocalDateTime now);

    void softDelete(Long couponId);
}