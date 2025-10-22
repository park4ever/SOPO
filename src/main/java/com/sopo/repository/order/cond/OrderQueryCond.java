package com.sopo.repository.order.cond;

import com.sopo.domain.order.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;

@Getter
@Builder(toBuilder = true)
public class OrderQueryCond {

    /** 조회 주체(구매자) */
    private final Long memberId;

    /** 조회 주체(판매자) -> 주문 내에 해당 판매자의 상품이 하나라도 포함되면 매칭 */
    private final Long sellerId;

    /** 주문 상태(다중) */
    @Builder.Default
    private final Set<OrderStatus> statuses = emptySet();

    /** 생성일 범위(포함) */
    private final LocalDate fromDate;
    private final LocalDate toDate;

    /** 총액 범위(옵션) */
    private final BigDecimal minTotalPrice;
    private final BigDecimal maxTotalPrice;

    /** 키워드(옵션) */
    private final String keyword;

    /** 키워드 대상 */
    @Builder.Default
    private final KeywordTarget keywordTarget = KeywordTarget.ITEM_NAME_OR_BRAND;

    /** 삭제 포함 여부 */
    @Builder.Default
    private final boolean includeDeleted = false;

    /** 정렬 스펙(화이트리스트) -> 없으면 Pageable.sort를 매핑, 둘 다 없으면 createdDate DESC */
    @Builder.Default
    private final List<OrderSortSpec> sorts = emptyList();
}