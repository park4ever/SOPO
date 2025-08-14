package com.sopo.config.auth;

import com.sopo.security.CustomUserDetails;
import com.sopo.security.session.MemberSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        session.setAttribute(LoginMemberArgumentResolver.SESSION_LOGIN_MEMBER_KEY,
                new MemberSession(
                        principal.getId(),
                        principal.getUsername(),    //email
                        principal.getName(),
                        principal.getRole()
                ));

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
