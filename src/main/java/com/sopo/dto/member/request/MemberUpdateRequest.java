package com.sopo.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(
        @NotBlank @Size(max = 20) String name,
        @NotBlank
        @Size(min = 8, max = 20)
        @Pattern(
                regexp = "^\\+?[\\d\\s\\-()]{8,20}$",
                message = "전화번호 형식이 올바르지 않습니다."
        )
        String phoneNumber
) {}
