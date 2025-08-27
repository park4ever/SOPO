package com.sopo.controller.view.member;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.member.response.MemberResponse;
import com.sopo.security.session.MemberSession;
import com.sopo.service.member.MemberService;
import com.sopo.domain.member.Role;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MemberController.class)
@ContextConfiguration(classes = { MemberController.class, MemberControllerTest.TestLoginMemberResolver.class })
class MemberControllerTest {

    @Configuration
    static class TestLoginMemberResolver {
        @Bean
        HandlerMethodArgumentResolver loginMemberResolver() {
            return new HandlerMethodArgumentResolver() {
                @Override
                public boolean supportsParameter(MethodParameter parameter) {
                    return parameter.hasParameterAnnotation(LoginMember.class)
                            && parameter.getParameterType().equals(MemberSession.class);
                }
                @Override
                public Object resolveArgument(MethodParameter parameter,
                                              @Nullable ModelAndViewContainer mavContainer,
                                              NativeWebRequest webRequest,
                                              @Nullable WebDataBinderFactory binderFactory) {
                    return new MemberSession(1L, "user@sopo.com", "사용자", Role.USER);
                }
            };
        }
    }

    @Autowired MockMvc mvc;

    @MockitoBean MemberService memberService;

    @Test
    @WithMockUser
    @DisplayName("내 정보 뷰 렌더링: 200 OK + 모델 포함")
    void render_me_view() throws Exception {
        var res = new MemberResponse(1L, "user@sopo.com", "사용자", "010-0000-0000", Role.USER, true);
        given(memberService.getMe(1L)).willReturn(res);

        mvc.perform(get("/members/me"))
                .andExpect(status().isOk())
                // 컨트롤러에서 반환하는 뷰 이름에 맞춰 조정하세요 (예: "member/me")
                .andExpect(view().name("member/me"))
                .andExpect(model().attributeExists("me"));
    }
}