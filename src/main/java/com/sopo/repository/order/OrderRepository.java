package com.sopo.repository.order;

import com.sopo.domain.order.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    Optional<Order> findByIdAndMemberId(Long orderId, Long memberId);

    boolean existsByIdAndMemberId(Long orderId, Long memberId);

    // 단건 상세 조회: N+1 방지를 위해 연관 로딩
    @EntityGraph(attributePaths = {
            "orderItems",
            "orderItems.itemOption",
            "orderItems.itemOption.item",
            "orderItems.itemOption.color",
            "orderItems.itemOption.size"
    })
    Optional<Order> findWithDetailsByIdAndMemberId(Long orderId, Long memberId);

    // 운영/관리자 단건 상세 (멤버 제한 없음)
    @EntityGraph(attributePaths = {
            "orderItems",
            "orderItems.itemOption",
            "orderItems.itemOption.item",
            "orderItems.itemOption.color",
            "orderItems.itemOption.size",
            "member"
    })
    Optional<Order> findWithDetailsById(Long orderId);
}