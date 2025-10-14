package com.sopo.dto.order.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateRequest(
        @NotEmpty(message = "주문 항목이 비어있습니다.")
        List<OrderLine> lines
) {
    public record OrderLine(
            @NotNull(message = "옵션 ID는 필수입니다.")
            Long optionId,
            @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
            int quantity
    ) {}
}