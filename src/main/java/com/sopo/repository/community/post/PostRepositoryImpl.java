package com.sopo.repository.community.post;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sopo.domain.community.post.Post;
import com.sopo.domain.community.post.QPost;
import com.sopo.domain.member.QMember;
import com.sopo.repository.community.post.cond.PostSearchCond;
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

import static com.sopo.domain.community.post.QPost.*;
import static com.sopo.domain.member.QMember.*;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> search(PostSearchCond cond, Pageable pageable) {
        List<Long> ids = queryFactory
                .select(post.id)
                .from(post)
                .where(buildPredicate(cond))
                .orderBy(buildSort(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Post> content = queryFactory
                .selectFrom(post)
                .join(post.author, member).fetchJoin()
                .where(post.id.in(ids))
                .orderBy(buildSort(pageable.getSort()))
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(post.count())
                .from(post)
                .where(buildPredicate(cond));

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private BooleanExpression buildPredicate(PostSearchCond cond) {
        if (cond == null) return null;
        return and(
                memberIdEq(cond.memberId()),
                createdBetween(cond.from(), cond.to()),
                keywordLike(cond.keyword())
        );
    }

    private BooleanExpression memberIdEq(Long memberId) {
        return (memberId == null) ? null : post.author.id.eq(memberId);
    }

    private BooleanExpression createdBetween(LocalDateTime from, LocalDateTime to) {
        BooleanExpression ge = (from == null) ? null : post.createdDate.goe(from);
        BooleanExpression le = (to == null) ? null : post.createdDate.loe(to);
        return and(ge, le);
    }

    private BooleanExpression keywordLike(String keyword) {
        if (!StringUtils.hasText(keyword)) return null;
        String kw = "%" + keyword.trim() + "%";
        return post.title.likeIgnoreCase(kw).or(post.content.likeIgnoreCase(kw));
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

    private OrderSpecifier<?>[] buildSort(Sort sort) {
        List<OrderSpecifier<?>> specs = new ArrayList<>();

        if (sort != null && sort.isSorted()) {
            for (Sort.Order o : sort) {
                Order direction = (o.getDirection().isAscending()) ? Order.ASC : Order.DESC;
                switch (o.getProperty()) {
                    //화이트리스트 정렬만 허용
                    case "createdDate" -> specs.add(new OrderSpecifier<>(direction, post.createdDate));
                    case "id" -> specs.add(new OrderSpecifier<>(direction, post.id));
                    default -> {
                        //허용되지 않은 필드는 무시
                    }
                }
            }
        }

        //기본 정렬
        if (specs.isEmpty()) {
            specs.add(new OrderSpecifier<>(Order.DESC, post.createdDate));
        }
        boolean hasId = specs.stream().anyMatch(s -> s.getTarget().equals(post.id));
        if (!hasId) {
            specs.add(new OrderSpecifier<>(Order.DESC, post.id));
        }

        return specs.toArray(OrderSpecifier[]::new);
    }
}