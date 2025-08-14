package com.sopo.dto.member.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberSignupRequest(
        @NotBlank @Email @Size(max = 30) String email,
        @NotBlank
        @Size(min = 8, max = 20)
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s])\\S{8,20}$",
            message = "비밀번호는 8~20자이며 소문자, 숫자, 특수문자를 각각 최소 1개 포함하고 공백은 허용되지 않습니다."
        )
        String password,
        @NotBlank @Size(max = 20) String name,
        @NotBlank
        @Size(min = 8, max = 20)
        @Pattern(
                regexp = "^\\+?[\\d\\s\\-()]{8,20}$",
                message = "전화번호 형식이 올바르지 않습니다."
        )
        String phoneNumber
) {}
