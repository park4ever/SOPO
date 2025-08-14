package com.sopo.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordChangeRequest(
        @NotBlank String currentPassword,
        @NotBlank
        @Size(min = 8, max = 20)
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s])\\S{8,20}$",
            message = "비밀번호는 8~20자이며 소문자, 숫자, 특수문자를 각각 최소 1개 포함하고 공백은 허용되지 않습니다."
        )
        String newPassword
) {}
