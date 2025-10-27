package com.sopo.dto.coupon.request;

import jakarta.validation.constraints.NotNull;

public record IssueRequest(
        @NotNull Long memberId,
        @NotNull Long couponId
) { }