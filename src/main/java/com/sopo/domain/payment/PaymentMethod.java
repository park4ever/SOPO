package com.sopo.domain.payment;

import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;

import java.util.Arrays;

public enum PaymentMethod {
    CARD("CARD"),
    ACCOUNT_TRANSFER("ACCOUNT_TRANSFER"),
    VIRTUAL_ACCOUNT("VIRTUAL_ACCOUNT"),
    MOBILE_PHONE("MOBILE_PHONE"),
    EASY_PAY("EASY_PAY");

    private final String code;
    PaymentMethod(String code) {
        this.code = code;
    }
    public String code() {
        return code;
    }

    public static PaymentMethod from(String raw) {
        if (raw == null) throw new BusinessException(ErrorCode.INVALID_PARAM);
        return Arrays.stream(values())
                .filter(m -> m.code.equalsIgnoreCase(raw.trim()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PARAM));
    }
}