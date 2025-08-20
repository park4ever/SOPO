package com.sopo.dto.address.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressUpdateRequest(
        @NotBlank @Size(max = 200) String roadAddress,
        @Size(max = 200) String landAddress,
        @Size(max = 100) String detailAddress,
        @NotBlank @Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다.")
        String zipcode
) {}