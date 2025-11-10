package com.sopo.dto.payment.request;

import com.sopo.domain.payment.EasyPayProvider;
import com.sopo.domain.payment.PaymentMethod;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Optional;

public record PaymentCreateRequest(
        @NotNull Long orderId,
        @NotBlank String paymentKey,
        @NotNull @Positive @Digits(integer = 10, fraction = 2)BigDecimal amount,
        @NotBlank String method,    //RAW 값(CARD, ACCOUNT_TRANSFER, EASY_PAY)
        @Nullable String easyPayProvider    //EASY_PAY 일 때만 사용(KAKAO_PAY 등)
) {
    public PaymentCreateRequest {
        if (paymentKey != null) paymentKey = paymentKey.trim();
        if (method != null) method = method.trim();
        if (easyPayProvider != null) easyPayProvider = easyPayProvider.trim();
    }

    /** enum으로 변환해서 서비스/도메인에서 안전하게 사용 */
    public PaymentMethod methodEnum() {
        return PaymentMethod.from(method);
    }

    /** 간편결제 제공자(enum) -> null이면 Optional.empty() */
    public Optional<EasyPayProvider> easyPayProviderEnum() {
        return EasyPayProvider.fromNullable(easyPayProvider);
    }
}