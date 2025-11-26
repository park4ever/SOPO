package com.sopo.repository.community.review;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sopo.domain.community.review.Review;
import com.sopo.repository.community.review.cond.ReviewSearchCond;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.sopo.domain.community.review.QReview.*;
import static com.sopo.domain.community.review.QReviewImage.reviewImage;

@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Review> search(ReviewSearchCond cond, Pageable pageable) {
        //정렬 스펙 한 번 생성 후 재사용
        OrderSpecifier<?>[] sortSpecifiers = buildSortSpecifiers(pageable.getSort());

        // 1) ID 페이지
        List<Long> ids = queryFactory
                .select(review.id)
                .from(review)
                .where(buildPredicate(cond))
                .orderBy(sortSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 2) 콘텐츠 로드 (이미지 fetch join)
        List<Review> content = queryFactory
                .selectFrom(review)
                .leftJoin(review.images, reviewImage).fetchJoin()
                .where(review.id.in(ids))
                .orderBy(sortSpecifiers)
                .fetch();

        // 3) count 쿼리 → PageableExecutionUtils로 마지막 페이지 최적화
        JPAQuery<Long> countQuery = queryFactory
                .select(review.count())
                .from(review)
                .where(buildPredicate(cond));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public long countByItemId(Long itemId) {
        Long cnt = queryFactory.select(review.id.count())
                .from(review)
                .where(eqItemId(itemId))
                .fetchOne();
        return (cnt == null) ? 0L : cnt;
    }

    @Override
    public Double findAverageRatingByItemId(Long itemId) {
        return queryFactory.select(review.rating.avg())
                .from(review)
                .where(eqItemId(itemId))
                .fetchOne();
    }

    private BooleanExpression buildPredicate(ReviewSearchCond c) {
        if (c == null) return null;
        return and(
                eqItemId(c.itemId()),
                eqMemberId(c.memberId()),
                geRating(c.minRating()),
                leRating(c.maxRating()),
                hasImage(c.hasImage()),
                createdBetween(c.from(), c.to())
        );
    }

    private BooleanExpression eqItemId(Long itemId) {
        return (itemId == null) ? null : review.item.id.eq(itemId);
    }
    private BooleanExpression eqMemberId(Long memberId) {
        return (memberId == null) ? null : review.member.id.eq(memberId);
    }
    private BooleanExpression geRating(Integer min) {
        return (min == null) ? null : review.rating.goe(min);
    }
    private BooleanExpression leRating(Integer max) {
        return (max == null) ? null : review.rating.loe(max);
    }
    private BooleanExpression hasImage(Boolean hasImage) {
        if (hasImage == null) return null;

        if (hasImage) {
            return review.images.isNotEmpty();
        } else {
            return review.images.isEmpty();
        }
    }

    private BooleanExpression createdBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null && to == null) return null;
        BooleanExpression ge = (from == null) ? null : review.createdDate.goe(from);
        BooleanExpression lt = (to == null) ? null : review.createdDate.lt(to);
        return and(ge, lt);
    }

    private BooleanExpression and(BooleanExpression a, BooleanExpression b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.and(b);
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

    private enum ReviewSortKey {
        CREATED_DATE,
        RATING,
        ID
    }

    private OrderSpecifier<?>[] buildSortSpecifiers(Sort pageableSort) {
        List<OrderSpecifier<?>> specs = new ArrayList<>();

        // Pageable.sort → 화이트리스트 매핑
        if (pageableSort != null && pageableSort.isSorted()) {
            for (Sort.Order o : pageableSort) {
                ReviewSortKey key = mapProperty(o.getProperty());
                OrderSpecifier<?> sp = toSpecifier(key, o.getDirection());
                if (sp != null) specs.add(sp);
            }
        }

        //기본 정렬: createdDate DESC
        if (specs.isEmpty()) {
            specs.add(new OrderSpecifier<>(Order.DESC, review.createdDate));
        }

        //tie-breaker: id DESC
        boolean hasIdSort = specs.stream().anyMatch(s -> s.getTarget().equals(review.id));
        if (!hasIdSort) {
            specs.add(new OrderSpecifier<>(Order.DESC, review.id));
        }

        return specs.toArray(OrderSpecifier[]::new);
    }

    private ReviewSortKey mapProperty(String property) {
        if (!StringUtils.hasText(property)) return null;
        return switch (property) {
            case "createdDate" -> ReviewSortKey.CREATED_DATE;
            case "rating"      -> ReviewSortKey.RATING;
            case "id"          -> ReviewSortKey.ID;
            default -> null;
        };
    }

    private OrderSpecifier<?> toSpecifier(ReviewSortKey key, Sort.Direction dir) {
        if (key == null) return null;
        Order d = (dir == Sort.Direction.ASC) ? Order.ASC : Order.DESC;
        return switch (key) {
            case CREATED_DATE -> new OrderSpecifier<>(d, review.createdDate);
            case RATING       -> new OrderSpecifier<>(d, review.rating);
            case ID           -> new OrderSpecifier<>(d, review.id);
        };
    }
}