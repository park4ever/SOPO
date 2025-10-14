package com.sopo.repository.item;

import com.sopo.domain.item.ItemOption;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemOptionRepository extends JpaRepository<ItemOption, Long> {

    // 주문 생성/취소 시 재고 변경에 사용 (초과 판매 방지)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select io from ItemOption io join fetch io.item where io.id = :id")
    Optional<ItemOption> findByIdForUpdate(@Param("id") Long id);

    // 주문 생성/취소 시 여러 옵션을 한 번에 락 (왕복/데드락 감소)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       select io
       from ItemOption io
       join fetch io.item
       where io.id in :ids
       order by io.id
       """)
    List<ItemOption> findAllByIdForUpdate(@Param("ids") List<Long> ids);
}