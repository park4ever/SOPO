package com.sopo.common.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** 통화 연산 표준화: 스케일/반올림 강제 (KRW 기준: 소수점 0) */
public record Money(BigDecimal amount) implements Comparable<Money> {
    public static final int SCALE = 0; // KRW=0, USD 등은 2로 변경 가능
    public static final RoundingMode RM = RoundingMode.HALF_UP;
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        if (amount == null) throw new IllegalArgumentException("amount is null");
        amount = amount.setScale(SCALE, RM);
    }

    public static Money of(long v) { return new Money(BigDecimal.valueOf(v)); }
    public static Money of(BigDecimal v) { return new Money(v); }

    public Money plus(Money other) {
        Objects.requireNonNull(other);
        return new Money(this.amount.add(other.amount));
    }

    public Money minus(Money other) {
        Objects.requireNonNull(other);
        return new Money(this.amount.subtract(other.amount));
    }

    public Money times(int qty) {
        if (qty < 0) throw new IllegalArgumentException("qty < 0");
        return new Money(this.amount.multiply(BigDecimal.valueOf(qty)));
    }

    public boolean isZero() { return amount.signum() == 0; }
    public BigDecimal asBigDecimal() { return amount; }

    @Override
    public int compareTo(Money o) {
        return this.amount.compareTo(o.amount);
    }
}