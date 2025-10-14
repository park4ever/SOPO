package com.sopo.service.order.processor;

import com.sopo.domain.item.ItemOption;
import com.sopo.domain.order.Order;
import com.sopo.domain.order.OrderItem;
import com.sopo.exception.BusinessException;
import com.sopo.repository.item.ItemOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sopo.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class OrderStockRestorer {

    private final ItemOptionRepository itemOptionRepository;

    /**
     * 취소 시 옵션 재고 복구 및 아이템 판매량 감소
     * - 옵션 ID를 일괄 수집하여 정렬
     * - IN 쿼리 한 번으로 PESSIMISTIC_WRITE 락 획득
     * - 고정된 순서로 복구 처리 (데드락/왕복 최소화)
     */
    public void restore(Order order) {
        //라인별 수량 집계 (optionId -> sum(qty))
        Map<Long, Integer> qtyByOption = order.getOrderItems().stream()
                .collect(Collectors.groupingBy(
                        oi -> oi.getItemOption().getId(),
                        Collectors.summingInt(OrderItem::getQuantity)
                ));

        if (qtyByOption.isEmpty()) return;

        //고정 순서 정렬
        List<Long> ids = qtyByOption.keySet().stream().sorted().toList();

        //한 번에 비관적 락 획득 (to-one인 item은 fetch join 되어 있음)
        Map<Long, ItemOption> locked = itemOptionRepository.findAllByIdForUpdate(ids).stream()
                .collect(Collectors.toMap(ItemOption::getId, io -> io));

        //복구 수행
        for (Long optionId : ids) {
            int qty = qtyByOption.get(optionId);
            ItemOption opt = Optional.ofNullable(locked.get(optionId))
                    .orElseThrow(() -> new BusinessException(OPTION_NOT_FOUND));

            opt.increaseStock(qty);
            opt.getItem().decreaseSalesVolume(qty);
        }
    }
}