package com.sopo.repository.community.qna;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sopo.domain.community.qna.QQuestion;
import com.sopo.domain.community.qna.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.sopo.domain.community.qna.QQuestion.*;
import static org.springframework.data.domain.Page.empty;

@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Question> searchPublicByItem(Long itemId, Pageable pageable) {
        List<Long> ids = queryFactory
                .select(question.id)
                .from(question)
                .where(
                        question.item.id.eq(itemId),
                        question.isPrivate.isFalse()
                )
                .orderBy(
                        question.createdDate.desc(),
                        question.id.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (ids.isEmpty()) {
            return empty(pageable);
        }

        List<Question> content = queryFactory
                .selectFrom(question)
                .join(question.asker).fetchJoin()
                .join(question.item).fetchJoin()
                .where(question.id.in(ids))
                .orderBy(
                        question.createdDate.desc(),
                        question.id.desc()
                )
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(question.count())
                .from(question)
                .where(
                        question.item.id.eq(itemId),
                        question.isPrivate.isFalse()
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    @Override
    public Page<Question> searchByMember(Long memberId, Pageable pageable) {
        List<Long> ids = queryFactory
                .select(question.id)
                .from(question)
                .where(question.asker.id.eq(memberId))
                .orderBy(
                        question.createdDate.desc(),
                        question.id.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Question> content = queryFactory
                .selectFrom(question)
                .join(question.item).fetchJoin()
                .where(question.id.in(ids))
                .orderBy(
                        question.createdDate.desc(),
                        question.id.desc()
                )
                .fetch();

        JPAQuery<Long> count = queryFactory
                .select(question.count())
                .from(question)
                .where(question.asker.id.eq(memberId));

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }
}