package com.sopo.dto.member.response;

import com.sopo.domain.member.Role;

public record MemberResponse(
        Long id,
        String email,
        String name,
        String phoneNumber,
        Role role,
        boolean enabled
) {}
