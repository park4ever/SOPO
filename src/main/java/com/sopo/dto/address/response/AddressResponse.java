package com.sopo.dto.address.response;

public record AddressResponse(
    Long id,
    String roadAddress,
    String landAddress,
    String detailAddress,
    String zipcode,
    boolean isDefault
) {}