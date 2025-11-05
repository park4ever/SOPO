package com.sopo.repository.delivery;

import com.sopo.domain.delivery.Delivery;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    Optional<Delivery> findByIdAndOrderMemberId(Long id, Long memberId);

    @EntityGraph(attributePaths = {"order", "address"})
    Optional<Delivery> findWithDetailsByIdAndOrderMemberId(Long id, Long memberId);
}