package com.sopo.repository.coupon;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sopo.domain.coupon.Coupon;
import com.sopo.domain.coupon.DiscountType;
import com.sopo.domain.coupon.QCoupon;
import com.sopo.repository.coupon.cond.CouponQueryCond;
import com.sopo.repository.coupon.cond.CouponSortKey;
import com.sopo.repository.coupon.cond.CouponSortSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.sopo.domain.coupon.QCoupon.*;
import static com.sopo.repository.coupon.cond.CouponSortKey.*;
import static org.springframework.data.domain.Sort.*;

@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Coupon> findActiveById(Long couponId, LocalDateTime now) {
        return Optional.ofNullable(
                queryFactory.selectFrom(coupon)
                        .where(
                                coupon.id.eq(couponId),
                                activeAt(now)
                        )
                        .fetchOne()
        );
    }

    @Override
    public List<Coupon> findActiveByIds(Collection<Long> couponIds, LocalDateTime now) {
        if (CollectionUtils.isEmpty(couponIds)) return List.of();
        return queryFactory.selectFrom(coupon)
                .where(
                        coupon.id.in(couponIds),
                        activeAt(now)
                )
                .fetch();
    }

    @Override
    public boolean existsActiveById(Long couponId, LocalDateTime now) {
        Integer one = queryFactory.selectOne()
                .from(coupon)
                .where(
                        coupon.id.eq(couponId),
                        activeAt(now)
                )
                .fetchFirst();
        return one != null;
    }

    @Override
    public Page<Coupon> search(CouponQueryCond cond, Pageable pageable, LocalDateTime now) {
        OrderSpecifier<?>[] sortSpecifiers = buildSortSpecifiers(cond, pageable.getSort());
        List<Long> ids = queryFactory.select(coupon.id)
                .from(coupon)
                .where(buildPredicate(cond, now))
                .orderBy(sortSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (ids.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Coupon> content = queryFactory.selectFrom(coupon)
                .where(coupon.id.in(ids))
                .orderBy(sortSpecifiers)
                .fetch();

        JPAQuery<Long> count = queryFactory.select(coupon.count())
                .from(coupon)
                .where(buildPredicate(cond, now));

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression buildPredicate(CouponQueryCond cond, LocalDateTime now) {
        if (cond == null) return null;
        return and(
                nameContains(cond.getNameContains()),
                discountTypeEq(cond.getDiscountType()),
                minOrderPriceBetween(cond.getMinOrderPriceMin(), cond.getMinOrderPriceMax()),
                hasCap(cond.getHasMaxDiscountCap()),
                cond.isActiveOnly() ? activeAt(now) : periodOverlap(cond.getPeriodFrom(), cond.getPeriodUntil())
        );
    }

    private BooleanExpression activeAt(LocalDateTime now) {
        if (now == null) return null;
        return coupon.validFrom.loe(now).and(coupon.validUntil.goe(now));
    }

    private BooleanExpression periodOverlap(LocalDateTime from, LocalDateTime until) {
        if (from == null && until == null) return null;
        BooleanExpression ge = (until == null) ? null : coupon.validFrom.loe(until);
        BooleanExpression le = (from == null) ? null : coupon.validUntil.goe(from);
        return and(ge, le);
    }

    private BooleanExpression nameContains(String kw) {
        if (kw == null || kw.isBlank()) return null;
        return coupon.name.containsIgnoreCase(kw);
    }

    private BooleanExpression discountTypeEq(DiscountType type) {
        if (type == null) return null;
        return coupon.discountType.eq(type);
    }

    private BooleanExpression minOrderPriceBetween(BigDecimal min, BigDecimal max) {
        BooleanExpression ge = (min == null) ? null : coupon.minOrderPrice.goe(min);
        BooleanExpression le = (max == null) ? null : coupon.minOrderPrice.loe(max);
        return and(ge, le);
    }

    private BooleanExpression hasCap(Boolean hasCap) {
        if (hasCap == null) return null;
        return hasCap ? coupon.maxDiscountAmount.isNotNull() : coupon.maxDiscountAmount.isNull();
    }

    private BooleanExpression and(BooleanExpression... exprs) {
        if (exprs == null || exprs.length == 0 ) return null;
        BooleanExpression result = null;
        for (BooleanExpression e : exprs) {
            if (e == null) continue;
            result = (result == null) ? e : result.and(e);
        }
        return result;
    }

    private OrderSpecifier<?>[] buildSortSpecifiers(CouponQueryCond cond, Sort pageableSort) {
        List<OrderSpecifier<?>> specs = new ArrayList<>();

        //cond에 정의된 화이트리스트 정렬 우선
        if (cond != null && !CollectionUtils.isEmpty(cond.getSorts())) {
            for (CouponSortSpec s : cond.getSorts()) {
                OrderSpecifier<?> sp = toSpecifier(s.key(), s.dir());
                if (sp != null) specs.add(sp);
            }
        }

        //없으면 Pageable.sort를 화이트리스트로 매핑
        else if (pageableSort != null && pageableSort.isSorted()) {
            for (Sort.Order o : pageableSort) {
                CouponSortKey key = mapProperty(o.getProperty());
                OrderSpecifier<?> sp = toSpecifier(key, o.getDirection());
                if (sp != null) specs.add(sp);
            }
        }

        //기본 정렬
        if (specs.isEmpty()) {
            specs.add(new OrderSpecifier<>(Order.DESC, coupon.validUntil));
        }

        boolean hasIdSort = specs.stream().anyMatch(s -> s.getTarget().equals(coupon.id));
        if (!hasIdSort) {
            specs.add(new OrderSpecifier<>(Order.DESC, coupon.id));
        }

        return specs.toArray(OrderSpecifier[]::new);
    }

    private CouponSortKey mapProperty(String property) {
        if (property == null) return null;
        return switch (property) {
            case "validUntil" -> VALID_UNTIL;
            case "validFrom" -> VALID_FROM;
            case "createdDate" -> CREATED_DATE;
            case "name" -> NAME;
            case "id" -> ID;
            default -> null;
        };
    }

    private OrderSpecifier<?> toSpecifier(CouponSortKey key, Direction dir) {
        if (key == null) return null;
        Order d = (dir == Direction.ASC) ? Order.ASC : Order.DESC;
        return switch (key) {
            case VALID_UNTIL -> new OrderSpecifier<>(d, coupon.validUntil);
            case VALID_FROM -> new OrderSpecifier<>(d, coupon.validFrom);
            case CREATED_DATE -> new OrderSpecifier<>(d, coupon.createdDate);
            case NAME -> new OrderSpecifier<>(d, coupon.name);
            case ID -> new OrderSpecifier<>(d, coupon.id);
        };
    }
}