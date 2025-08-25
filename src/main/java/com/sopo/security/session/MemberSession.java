package com.sopo.security.session;

import com.sopo.domain.member.Role;
import lombok.Getter;

public record MemberSession(
        Long id,
        String email,
        String name,
        Role role
) {}