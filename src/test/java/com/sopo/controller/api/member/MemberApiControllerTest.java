package com.sopo.controller.api.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopo.config.auth.LoginMember;
import com.sopo.domain.member.Role;
import com.sopo.dto.member.request.MemberSignupRequest;
import com.sopo.dto.member.request.MemberUpdateRequest;
import com.sopo.dto.member.request.PasswordChangeRequest;
import com.sopo.dto.member.response.MemberResponse;

// 퍼사드 + 중첩 DTO
import com.sopo.dto.member.request.MemberSignupWithAddressRequest;
import com.sopo.dto.address.request.AddressCreateRequest;

import com.sopo.facade.member.MemberRegistrationFacade;
import com.sopo.security.session.MemberSession;
import com.sopo.service.member.MemberService;

import jakarta.annotation.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MemberApiController.class)
@ContextConfiguration(classes = { MemberApiController.class, MemberApiControllerTest.TestLoginMemberResolver.class })
class MemberApiControllerTest {

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
    @Autowired ObjectMapper om;

    @MockitoBean MemberService memberService;
    @MockitoBean MemberRegistrationFacade memberRegistrationFacade;

    @Test
    @DisplayName("회원가입: 201 Created")
    void signup() throws Exception {
        var req = new MemberSignupRequest("new@sopo.com", "p@ssw0rd!", "새유저", "010-1111-2222");
        given(memberService.register(ArgumentMatchers.any())).willReturn(100L);

        mvc.perform(post("/api/members")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/members/100"));
    }

    @Test
    @DisplayName("회원가입+주소 퍼사드: 201 Created")
    void signup_with_address() throws Exception {
        // ✅ 중첩 DTO로 정확히 생성
        var member = new MemberSignupRequest("new@sopo.com", "p@ssw0rd!", "새유저", "010-1111-2222");
        var address = new AddressCreateRequest("도로", "지번", "상세", "01234", true);
        var req = new MemberSignupWithAddressRequest(member, address);

        // ✅ 퍼사드 메서드명 수정: registerWithAddress
        given(memberRegistrationFacade.registerWithAddress(ArgumentMatchers.any())).willReturn(101L);

        mvc.perform(post("/api/members/with-address")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/members/101"));

        verify(memberRegistrationFacade).registerWithAddress(ArgumentMatchers.any());
    }

    @Test
    @WithMockUser
    @DisplayName("내 정보 조회: 200 OK")
    void get_me() throws Exception {
        var res = new MemberResponse(1L, "user@sopo.com", "사용자", "010-0000-0000", Role.USER, true);
        given(memberService.getMe(1L)).willReturn(res);

        mvc.perform(get("/api/members/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@sopo.com"));

        verify(memberService).getMe(1L);
    }

    @Test
    @WithMockUser
    @DisplayName("내 정보 수정: 204 No Content")
    void update_me() throws Exception {
        var req = new MemberUpdateRequest("수정유저", "010-9999-9999");

        mvc.perform(patch("/api/members/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        verify(memberService).updateMe(1L, req);
    }

    @Test
    @WithMockUser
    @DisplayName("비밀번호 변경: 204 No Content")
    void change_password() throws Exception {
        var req = new PasswordChangeRequest("currentP@ss1!", "newP@ss1!");

        mvc.perform(patch("/api/members/me/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        verify(memberService).changePassword(1L, req);
    }

    @Test
    @WithMockUser
    @DisplayName("회원 탈퇴: 204 No Content")
    void withdraw() throws Exception {
        mvc.perform(delete("/api/members/me")
                        .with(csrf())
                        .param("password", "currentP@ss1!"))
                .andExpect(status().isNoContent());

        verify(memberService).withdraw(1L, "currentP@ss1!");
    }
}