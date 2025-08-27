package com.sopo.controller.api.address;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopo.config.auth.LoginMember;
import com.sopo.dto.address.request.AddressCreateRequest;
import com.sopo.dto.address.request.AddressUpdateRequest;
import com.sopo.dto.address.response.AddressResponse;
import com.sopo.security.session.MemberSession;
import com.sopo.service.address.AddressService;
import com.sopo.domain.member.Role;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AddressApiController.class)
@Import(AddressApiControllerTest.LoginMemberResolverConfig.class)
public class AddressApiControllerTest {

    @TestConfiguration
    static class LoginMemberResolverConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(java.util.List<org.springframework.web.method.support.HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new org.springframework.web.method.support.HandlerMethodArgumentResolver() {
                @Override
                public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
                    return parameter.hasParameterAnnotation(com.sopo.config.auth.LoginMember.class)
                            && parameter.getParameterType().equals(com.sopo.security.session.MemberSession.class);
                }
                @Override
                public Object resolveArgument(org.springframework.core.MethodParameter parameter,
                                              org.springframework.web.method.support.ModelAndViewContainer mavContainer,
                                              org.springframework.web.context.request.NativeWebRequest webRequest,
                                              org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
                    return new com.sopo.security.session.MemberSession(
                            1L, "user@sopo.com", "테스트", com.sopo.domain.member.Role.USER);
                }
            });
        }
    }

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockitoBean AddressService addressService;

    @Test
    @WithMockUser
    @DisplayName("주소 생성 : 201 Created + Location")
    void create_address() throws Exception {
        var req = new AddressCreateRequest("도로명", "지번", "상세", "01234", true);
        given(addressService.add(ArgumentMatchers.eq(1L), ArgumentMatchers.any())).willReturn(10L);

        mvc.perform(post("/api/addresses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/addresses/10"));

        verify(addressService).add(1L, req);
    }

    @Test
    @WithMockUser
    @DisplayName("주소 목록: 200 OK")
    void list_addresses() throws Exception {
        var res = new AddressResponse(10L, "도로", "지번", "상세", "01234", true);
        given(addressService.list(1L)).willReturn(List.of(res));

        mvc.perform(get("/api/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    @WithMockUser
    @DisplayName("주소 단건: 200 OK")
    void get_address() throws Exception {
        var res = new AddressResponse(10L, "도로", null, null, null, false);
        given(addressService.get(1L, 10L)).willReturn(res);

        mvc.perform(get("/api/addresses/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    @WithMockUser
    @DisplayName("주소 수정: 204 No Content")
    void update_address() throws Exception {
        var req = new AddressUpdateRequest("도로2", "지번2", "상세2", "99999");

        mvc.perform(patch("/api/addresses/{id}", 10L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        verify(addressService).update(1L, 10L, req);
    }

    @Test
    @WithMockUser
    @DisplayName("기본지 전환: 204 No Content")
    void set_default() throws Exception {
        mvc.perform(patch("/api/addresses/{id}/default", 10L).with(csrf()))
                .andExpect(status().isNoContent());

        verify(addressService).setDefault(1L, 10L);
    }

    @Test
    @WithMockUser
    @DisplayName("주소 삭제: 204 No Content")
    void delete_address() throws Exception {
        mvc.perform(delete("/api/addresses/{id}", 10L).with(csrf()))
                .andExpect(status().isNoContent());

        verify(addressService).remove(1L, 10L);
    }
}