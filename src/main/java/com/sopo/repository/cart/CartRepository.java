package com.sopo.repository.cart;

import com.sopo.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByMemberId(Long memberId);
    boolean existsByMemberId(Long memberId);
}