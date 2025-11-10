package com.sopo.domain.payment;

import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;

import java.util.Optional;

public enum EasyPayProvider {
    KAKAO_PAY, TOSS_PAY, NAVER_PAY, PAYCO, APPLE_PAY, SAMSUNG_PAY;

    public static Optional<EasyPayProvider> fromNullable(String raw) {
        if (raw == null || raw.isBlank()) return Optional.empty();
        try {
            return Optional.of(EasyPayProvider.valueOf(raw.trim().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }
    }
}