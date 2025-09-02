package com.sopo.security.aop;

import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@RequiredArgsConstructor
@Order(0)
@Component
public class AdminOnlyAspect {

    private final CurrentUserProvider currentUser;

    @Before("@annotation(com.sopo.security.aop.AdminOnly) || @within(com.sopo.security.aop.AdminOnly)")
    public void checkAdminRole() {
        if (!currentUser.hasRole("ROLE_ADMIN")) {
            throw new BusinessException(ErrorCode.FORBIDDEN_OPERATION);
        }
    }
}