package com.sopo.service.order.processor;

import com.sopo.domain.item.Item;
import com.sopo.domain.item.ItemOption;
import com.sopo.domain.item.ItemStatus;
import com.sopo.domain.order.OrderItem;
import com.sopo.dto.order.request.OrderCreateRequest;
import com.sopo.exception.BusinessException;
import com.sopo.repository.item.ItemOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sopo.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class OrderPlacementProcessor {

    private final ItemOptionRepository itemOptionRepository;

    /**
     * 주문 라인들을 옵션별로 합산 → (정렬) → 비관적 락 일괄 획득 → 검증
     * → 재고 차감/판매량 증가 → OrderItem 생성
     */
    public List<OrderItem> buildOrderItems(List<OrderCreateRequest.OrderLine> lines) {
        if (lines == null || lines.isEmpty()) throw new BusinessException(ORDER_EMPTY_LINES);

        // 1) 같은 옵션 중복 라인 합산
        Map<Long, Integer> qtyByOption = aggregate(lines);
        if (qtyByOption.isEmpty()) throw new BusinessException(ORDER_EMPTY_LINES);

        // 2) 고정 순서 정렬
        List<Long> ids = qtyByOption.keySet().stream().sorted().toList();

        // 3) 한 번에 비관적 락 획득 (to-one인 item은 fetch join 되어 있음)
        Map<Long, ItemOption> locked = itemOptionRepository.findAllByIdForUpdate(ids).stream()
                .collect(Collectors.toMap(ItemOption::getId, io -> io));

        // 4) 검증/차감/판매량 증가/라인 생성
        List<OrderItem> orderItems = new ArrayList<>(ids.size());
        for (Long optionId : ids) {
            int quantity = qtyByOption.get(optionId);
            if (quantity < 1) throw new BusinessException(INVALID_QUANTITY); // 컨트롤러 @Valid가 있어도 방어 차원 유지 가능

            ItemOption opt = Optional.ofNullable(locked.get(optionId))
                    .orElseThrow(() -> new BusinessException(OPTION_NOT_FOUND));

            Item item = opt.getItem();
            if (item.isDeleted()) throw new BusinessException(ITEM_DELETED);
            if (item.getStatus() != ItemStatus.ON_SALE) throw new BusinessException(ITEM_NOT_ON_SALE);

            if (opt.isSoldOut()) throw new BusinessException(OPTION_SOLD_OUT);
            if (opt.getStock() < quantity) throw new BusinessException(QUANTITY_EXCEEDS_STOCK);

            opt.decreaseStock(quantity);
            item.increaseSalesVolume(quantity);

            // 가격 스냅샷 및 합계 계산은 도메인 팩토리에서 Money 규칙으로 처리
            orderItems.add(OrderItem.create(opt, quantity));
        }
        return orderItems;
    }

    private Map<Long, Integer> aggregate(List<OrderCreateRequest.OrderLine> lines) {
        return lines.stream().collect(Collectors.toMap(
                OrderCreateRequest.OrderLine::optionId,
                OrderCreateRequest.OrderLine::quantity,
                Integer::sum
        ));
    }
}