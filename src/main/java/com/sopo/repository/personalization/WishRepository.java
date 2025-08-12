package com.sopo.repository.personalization;

import com.sopo.domain.personalization.wish.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {
}
