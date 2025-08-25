package com.sopo.dto.member.request;

import com.sopo.dto.address.request.AddressCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record MemberSignupWithAddressRequest(
        @NotNull @Valid MemberSignupRequest member,
        @NotNull @Valid AddressCreateRequest address
) {}