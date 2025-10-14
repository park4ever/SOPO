package com.sopo.security.aop;

import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@RequiredArgsConstructor
@Order(0)
@Component
public class AdminOnlyAspect {

    private final CurrentUserProvider currentUser;

    @Before("@annotation(adminOnly) || @within(adminOnly)")
    public void checkAdminRole(AdminOnly adminOnly) {
        boolean ok = Arrays.stream(adminOnly.value()).anyMatch(currentUser::hasRole);
        if (!ok) throw new BusinessException(ErrorCode.FORBIDDEN_OPERATION);
    }
}