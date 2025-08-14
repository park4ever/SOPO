package com.sopo.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    /* TODO 세션에 보관된 로그인 회원 식별자(또는 Member 요약 DTO)를 주입하는 해석기.
        실제 세션 키/타입은 MemberService 구현 시 확정.
     */

    public static final String SESSION_LOGIN_MEMBER_KEY = "LOGIN_MEMBER";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request != null ? request.getSession(false) : null;
        return (session != null) ? session.getAttribute(SESSION_LOGIN_MEMBER_KEY) : null;
    }
}