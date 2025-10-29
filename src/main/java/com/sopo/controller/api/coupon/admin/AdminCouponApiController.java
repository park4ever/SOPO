package com.sopo.controller.api.coupon.admin;

import com.sopo.dto.coupon.request.CreateFixedCouponRequest;
import com.sopo.dto.coupon.request.CreateRateCouponRequest;
import com.sopo.dto.coupon.request.PreviewCouponRequest;
import com.sopo.dto.coupon.response.CouponDetailResponse;
import com.sopo.dto.coupon.response.CouponSummaryResponse;
import com.sopo.repository.coupon.cond.CouponQueryCond;
import com.sopo.security.aop.AdminOnly;
import com.sopo.service.coupon.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupons")
@AdminOnly
public class AdminCouponApiController {

    private final CouponService couponService;

    @PostMapping("/fixed")
    public ResponseEntity<?> createFixed(@RequestBody @Valid CreateFixedCouponRequest request) {
        Long id = couponService.createFixed(request);
        return ResponseEntity.created(URI.create("/api/admin/coupons/" + id)).body(id);
    }

    @PostMapping("/rate")
    public ResponseEntity<?> createRate(@RequestBody @Valid CreateRateCouponRequest request) {
        Long id = couponService.createRate(request);
        return ResponseEntity.created(URI.create("/api/admin/coupons" + id)).body(id);
    }

    @GetMapping("/{id}")
    public CouponDetailResponse get(@PathVariable("id") Long id) {
        return couponService.get(id);
    }

    @PostMapping("/{id}/preview")
    public BigDecimal preview(@PathVariable("id") Long id,
                              @RequestBody @Valid PreviewCouponRequest request) {
        return couponService.preview(id, request, LocalDateTime.now());
    }

    @GetMapping("/search")
    public Page<CouponSummaryResponse> search(CouponQueryCond cond, Pageable pageable) {
        return couponService.search(cond, pageable, LocalDateTime.now());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        couponService.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}