package com.sopo.repository.order;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sopo.domain.order.Order;
import com.sopo.domain.order.OrderStatus;
import com.sopo.repository.order.cond.KeywordTarget;
import com.sopo.repository.order.cond.OrderQueryCond;
import com.sopo.repository.order.cond.OrderSortKey;
import com.sopo.repository.order.cond.OrderSortSpec;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.querydsl.core.types.Order.ASC;
import static com.querydsl.core.types.Order.DESC;
import static com.sopo.domain.item.QItem.*;
import static com.sopo.domain.item.QItemOption.*;
import static com.sopo.domain.order.QOrder.*;
import static com.sopo.domain.order.QOrderItem.*;
import static com.sopo.repository.order.cond.OrderSortKey.*;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> searchOrders(OrderQueryCond cond, Pageable pageable) {
        // 정렬 스펙은 한 번만 생성해서 재사용
        OrderSpecifier<?>[] sortSpecifiers = buildSortSpecifiers(cond, pageable.getSort());

        //ID 페이지
        List<Long> ids = queryFactory
                .select(order.id)
                .from(order)
                .where(buildPredicate(cond))
                .orderBy(sortSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        /**콘텐츠 로드
         * -to-one(member)만 fetch join : 목록에서 안전하게 N+1 완화*/
        List<Order> content = queryFactory
                .selectFrom(order)
                .leftJoin(order.member).fetchJoin()
                .where(order.id.in(ids))
                .orderBy(sortSpecifiers)
                .fetch();

        //카운트 -> 마지막 페이지에선 실행 생략 가능
        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .where(buildPredicate(cond));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression buildPredicate(OrderQueryCond c) {
        return and(
                memberIdEq(c.getMemberId()),
                sellerIdExists(c.getSellerId()),
                deletedFilter(c.isIncludeDeleted()),
                statusesIn(c.getStatuses()),
                createdBetween(c.getFromDate(), c.getToDate()),
                totalPriceBetween(c.getMinTotalPrice(), c.getMaxTotalPrice()),
                keywordPredicate(c.getKeyword(), c.getKeywordTarget())
        );
    }

    private BooleanExpression memberIdEq(@Nullable Long memberId) {
        return (memberId == null) ? null : order.member.id.eq(memberId);
    }

    /** 주문 내에 해당 판매자의 상품이 하나라도 포함되면 매칭(SELLER 관점 조회 대비) */
    private BooleanExpression sellerIdExists(@Nullable Long sellerId) {
        if (sellerId == null) return null;
        return JPAExpressions.selectOne()
                .from(orderItem)
                .join(orderItem.itemOption, itemOption)
                .join(itemOption.item, item)
                .where(
                        orderItem.order.eq(order),
                        item.seller.id.eq(sellerId)
                )
                .exists();
    }

    private BooleanExpression deletedFilter(boolean includeDeleted) {
        return includeDeleted ? null : order.isDeleted.isFalse();
    }

    private BooleanExpression statusesIn(@Nullable Set<OrderStatus> statuses) {
        return CollectionUtils.isEmpty(statuses) ? null : order.status.in(statuses);
    }

    private BooleanExpression createdBetween(@Nullable LocalDate from, @Nullable LocalDate to) {
        BooleanExpression ge = (from == null) ? null : order.createdDate.goe(from.atStartOfDay());
        BooleanExpression lt = (to == null) ? null : order.createdDate.lt(to.plusDays(1).atStartOfDay());
        return and(ge, lt);
    }

    private BooleanExpression totalPriceBetween(@Nullable BigDecimal min, @Nullable BigDecimal max) {
        BooleanExpression ge = (min == null) ? null : order.totalPrice.goe(min);
        BooleanExpression le = (max == null) ? null : order.totalPrice.loe(max);
        return and(ge, le);
    }

    private BooleanExpression keywordPredicate(@Nullable String kw, @Nullable KeywordTarget target) {
        if (!StringUtils.hasText(kw) || target == null) return null;

        return switch (target) {
            case ITEM_NAME_OR_BRAND -> existsItemName(kw).or(existsItemBrand(kw));
            case ITEM_NAME          -> existsItemName(kw);
            case ITEM_BRAND         -> existsItemBrand(kw);
            case BUYER_NAME         -> order.member.name.containsIgnoreCase(kw);
            case BUYER_EMAIL        -> order.member.email.containsIgnoreCase(kw);
        };
    }

    private BooleanExpression existsItemName(String kw) {
        return JPAExpressions.selectOne()
                .from(orderItem)
                .join(orderItem.itemOption, itemOption)
                .join(itemOption.item, item)
                .where(
                        orderItem.order.eq(order),
                        item.name.containsIgnoreCase(kw)
                )
                .exists();
    }

    private BooleanExpression existsItemBrand(String kw) {
        return JPAExpressions.selectOne()
                .from(orderItem)
                .join(orderItem.itemOption, itemOption)
                .join(itemOption.item, item)
                .where(
                        orderItem.order.eq(order),
                        item.brand.containsIgnoreCase(kw)
                )
                .exists();
    }

    private BooleanExpression and(@Nullable BooleanExpression a, @Nullable BooleanExpression b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.and(b);
    }

    private BooleanExpression and(@Nullable BooleanExpression... exprs) {
        if (exprs == null || exprs.length == 0) return null;
        BooleanExpression result = null;
        for (BooleanExpression e : exprs) {
            if (e == null) continue;
            result = (result == null) ? e : result.and(e);
        }
        return result;
    }

    /**
     * cond.sorts 우선 -> 없으면 pageable.sort 매핑 -> 기본(createdDate DESC) +
     * 이미 ID 정렬이 있으면 tie-breaker 생략
     */
    private OrderSpecifier<?>[] buildSortSpecifiers(OrderQueryCond c, Sort pageableSort) {
        List<OrderSpecifier<?>> specs = new ArrayList<>();

        //cond.sorts(화이트리스트 키)
        if (!CollectionUtils.isEmpty(c.getSorts())) {
            for (OrderSortSpec s : c.getSorts()) {
                OrderSpecifier<?> sp = toSpecifier(s.key(), s.dir());
                if (sp != null) specs.add(sp);
            }
        } else if (pageableSort != null && pageableSort.isSorted()) {
            //pageable.sort -> 화이트리스트 매핑
            for (Sort.Order o : pageableSort) {
                OrderSortKey key = mapProperty(o.getProperty());
                OrderSpecifier<?> sp = toSpecifier(key, o.getDirection());
                if (sp != null) specs.add(sp);
            }
        }

        //기본값
        if (specs.isEmpty()) {
            specs.add(new OrderSpecifier<>(DESC, order.createdDate));
        }
        //tie-breaker(id)
        boolean hasIdSort = specs.stream().anyMatch(s -> s.getTarget().equals(order.id));
        if (!hasIdSort) {
            specs.add(new OrderSpecifier<>(DESC, order.id));
        }

        return specs.toArray(OrderSpecifier[]::new);
    }

    private OrderSortKey mapProperty(String property) {
        return switch (property) {
            case "createdDate" -> CREATED_DATE;
            case "totalPrice" -> TOTAL_PRICE;
            case "status" -> STATUS;
            case "id" -> ID;
            default -> null;
        };
    }

    private OrderSpecifier<?> toSpecifier(@Nullable OrderSortKey key, Direction dir) {
        if (key == null) return null;
        var d = (dir == Direction.ASC) ? ASC : DESC;
        return switch (key) {
            case CREATED_DATE -> new OrderSpecifier<>(d, order.createdDate);
            case TOTAL_PRICE -> new OrderSpecifier<>(d, order.totalPrice);
            case STATUS -> new OrderSpecifier<>(d, order.status);
            case ID -> new OrderSpecifier<>(d, order.id);
        };
    }
}