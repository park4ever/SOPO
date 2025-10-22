package com.sopo.repository.coupon;

import com.sopo.domain.coupon.Coupon;
import com.sopo.repository.coupon.cond.CouponQueryCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CouponRepositoryCustom {

    Optional<Coupon> findActiveById(Long couponId, LocalDateTime now);
    List<Coupon> findActiveByIds(Collection<Long> couponIds, LocalDateTime now);
    boolean existsActiveById(Long couponId, LocalDateTime now);

    Page<Coupon> search(CouponQueryCond cond, Pageable pageable, LocalDateTime now);
}