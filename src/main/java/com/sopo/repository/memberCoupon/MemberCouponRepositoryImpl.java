package com.sopo.repository.memberCoupon;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sopo.domain.coupon.MemberCoupon;
import com.sopo.domain.coupon.MemberCouponStatus;
import com.sopo.domain.coupon.QCoupon;
import com.sopo.domain.coupon.QMemberCoupon;
import com.sopo.repository.memberCoupon.cond.MemberCouponQueryCond;
import com.sopo.repository.memberCoupon.cond.MemberCouponSortKey;
import com.sopo.repository.memberCoupon.cond.MemberCouponSortSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.sopo.domain.coupon.MemberCouponStatus.*;
import static com.sopo.domain.coupon.QCoupon.*;
import static com.sopo.domain.coupon.QMemberCoupon.*;
import static com.sopo.repository.memberCoupon.cond.MemberCouponSortKey.*;
import static org.springframework.data.domain.Sort.*;

@RequiredArgsConstructor
public class MemberCouponRepositoryImpl implements MemberCouponRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<MemberCoupon> findUsableByIdAndMember(Long memberCouponId, Long memberId, LocalDateTime now) {
        return Optional.ofNullable(
                queryFactory.selectFrom(memberCoupon)
                        .join(memberCoupon.coupon, coupon).fetchJoin()
                        .where(
                                memberCoupon.id.eq(memberCouponId),
                                memberCoupon.member.id.eq(memberId),
                                memberCoupon.status.eq(ISSUED),
                                coupon.validFrom.loe(now),
                                coupon.validUntil.goe(now)
                        )
                        .fetchOne()
        );
    }

    @Override
    public List<Long> findIdsToExpire(LocalDateTime now, int limit) {
        return queryFactory.select(memberCoupon.id)
                .from(memberCoupon)
                .where(expireTarget(now))
                .orderBy(memberCoupon.id.asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public boolean existsIssuedByMemberAndCoupon(Long memberId, Long couponId) {
        Integer one = queryFactory.selectOne()
                .from(memberCoupon)
                .where(
                        memberCoupon.member.id.eq(memberId),
                        memberCoupon.coupon.id.eq(couponId),
                        memberCoupon.status.eq(ISSUED)
                )
                .fetchFirst();

        return one != null;
    }

    @Override
    public Page<MemberCoupon> search(MemberCouponQueryCond cond, Pageable pageable, LocalDateTime now) {
        OrderSpecifier<?>[] sortSpecifiers = buildSortSpecifiers(cond, pageable.getSort());
        List<Long> ids = queryFactory.select(memberCoupon.id)
                .from(memberCoupon)
                .where(buildPredicate(cond, now))
                .orderBy(sortSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (ids.isEmpty()) {
            return Page.empty(pageable);
        }

        List<MemberCoupon> content = queryFactory.selectFrom(memberCoupon)
                .join(memberCoupon.coupon, coupon).fetchJoin()
                .where(memberCoupon.id.in(ids))
                .orderBy(sortSpecifiers)
                .fetch();

        JPAQuery<Long> count = queryFactory.select(memberCoupon.count())
                .from(memberCoupon)
                .where(buildPredicate(cond, now));

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression buildPredicate(MemberCouponQueryCond cond, LocalDateTime now) {
        if (cond == null) return null;
        return and(
                memberIdEq(cond.getMemberId()),
                couponIdEq(cond.getCouponId()),
                statusesIn(cond.getStatuses()),
                issuedBetween(cond.getIssuedFrom(), cond.getIssuedUntil()),
                usedBetween(cond.getUsedFrom(), cond.getUsedUntil()),
                cond.isActivePolicyOnly() ? activePolicy(now) : null,
                cond.isExpireTargetOnly() ? expireTarget(now) : null
        );
    }

    private BooleanExpression memberIdEq(Long memberId) {
        if (memberId == null) return null;
        return memberCoupon.member.id.eq(memberId);
    }

    private BooleanExpression couponIdEq(Long couponId) {
        if (couponId == null) return null;
        return memberCoupon.coupon.id.eq(couponId);
    }

    private BooleanExpression statusesIn(Set<MemberCouponStatus> statuses) {
        if (CollectionUtils.isEmpty(statuses)) return null;
        return memberCoupon.status.in(statuses);
    }

    private BooleanExpression issuedBetween(LocalDateTime from, LocalDateTime until) {
        BooleanExpression ge = (from == null) ? null : memberCoupon.issuedAt.goe(from);
        BooleanExpression le = (until == null) ? null : memberCoupon.issuedAt.loe(until);
        return and(ge, le);
    }

    private BooleanExpression usedBetween(LocalDateTime from, LocalDateTime until) {
        BooleanExpression ge = (from == null) ? null : memberCoupon.usedAt.goe(from);
        BooleanExpression le = (until == null) ? null : memberCoupon.usedAt.loe(until);
        return and(ge, le);
    }

    private BooleanExpression activePolicy(LocalDateTime now) {
        if (now == null) return null;
        return memberCoupon.coupon.validFrom.loe(now).and(memberCoupon.coupon.validUntil.goe(now));
    }

    private BooleanExpression expireTarget(LocalDateTime now) {
        if (now == null) return null;
        return memberCoupon.status.in(ISSUED, CANCELED)
                .and(memberCoupon.coupon.validUntil.lt(now));
    }

    private BooleanExpression and(BooleanExpression... exprs) {
        if (exprs == null || exprs.length == 0) return null;
        BooleanExpression result = null;
        for (BooleanExpression e : exprs) {
            if (e == null) continue;
            result = (result == null) ? e : result.and(e);
        }
        return result;
    }

    private OrderSpecifier<?>[] buildSortSpecifiers(MemberCouponQueryCond cond, Sort pageableSort) {
        List<OrderSpecifier<?>> specs = new ArrayList<>();

        //cond 정렬 우선
        if (cond != null && !CollectionUtils.isEmpty(cond.getSorts())) {
            for (MemberCouponSortSpec s : cond.getSorts()) {
                OrderSpecifier<?> sp = toSpecifier(s.key(), s.dir());
                if (sp != null) specs.add(sp);
            }
        }

        //없으면 Pageable.sort 매핑
        else if (pageableSort != null && pageableSort.isSorted()) {
            for (Sort.Order o : pageableSort) {
                MemberCouponSortKey key = mapProperty(o.getProperty());
                OrderSpecifier<?> sp = toSpecifier(key, o.getDirection());
                if (sp != null) specs.add(sp);
            }
        }

        //기본 정렬 : issuedAt DESC
        if (specs.isEmpty()) {
            specs.add(new OrderSpecifier<>(Order.DESC, memberCoupon.issuedAt));
        }

        //tie-breaker : id DESC
        boolean hasIdSort = specs.stream().anyMatch(s -> s.getTarget().equals(memberCoupon.id));
        if (!hasIdSort) {
            specs.add(new OrderSpecifier<>(Order.DESC, memberCoupon.id));
        }

        return specs.toArray(OrderSpecifier[]::new);
    }

    private MemberCouponSortKey mapProperty(String property) {
        if (property == null) return null;
        return switch (property) {
            case "issuedAt" -> ISSUED_AT;
            case "usedAt" -> USED_AT;
            case "status" -> STATUS;
            case "id" -> ID;
            default -> null;
        };
    }

    private OrderSpecifier<?> toSpecifier(MemberCouponSortKey key, Direction dir) {
        if (key == null) return null;
        Order d = (dir == Direction.ASC) ? Order.ASC : Order.DESC;
        return switch (key) {
            case ISSUED_AT -> new OrderSpecifier<>(d, memberCoupon.issuedAt);
            case USED_AT -> new OrderSpecifier<>(d, memberCoupon.usedAt);
            case STATUS -> new OrderSpecifier<>(d, memberCoupon.status);
            case ID -> new OrderSpecifier<>(d, memberCoupon.id);
        };
    }
}