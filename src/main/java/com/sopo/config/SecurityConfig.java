package com.sopo.config;

import com.sopo.config.auth.LoginSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.time.LocalDateTime;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // ✅ /api/** 인지 여부만 판단하는, 의존성-프리 RequestMatcher
        RequestMatcher apiMatcher = (HttpServletRequest request) -> {
            String uri = request.getRequestURI();            // e.g. /sopo/api/addresses
            String ctx = request.getContextPath();           // e.g. /sopo  (없으면 "")
            String path = (ctx == null || ctx.isEmpty()) ? uri : uri.substring(ctx.length());
            return path.startsWith("/api/");
        };

        http
                // 세션 기반
                .securityContext(Customizer.withDefaults())
                .sessionManagement(Customizer.withDefaults())

                // ✅ CSRF 전역 유지 (동일 오리진 Ajax는 헤더로 토큰 전달)
                //    만약 당장 테스트 편의를 위해 API만 끄고 싶으면 아래 주석 해제:
                // .csrf(csrf -> csrf.ignoringRequestMatchers(apiMatcher))
                .csrf(Customizer.withDefaults())

                // 인가 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/logout",
                                "/css/**", "/js/**", "/images/**").permitAll()

                        // ✅ 회원가입 API(익명 허용)
                        .requestMatchers(HttpMethod.POST, "/api/members", "/api/members/with-address").permitAll()

                        // 인증 필요
                        .requestMatchers("/members/**", "/api/members/me/**", "/api/addresses/**").authenticated()

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/seller/**").hasAnyRole("SELLER", "ADMIN")

                        .anyRequest().authenticated()
                )

                // ✅ API 구간: 401/403을 JSON으로 반환 (리다이렉트 금지)
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                (request, response, authEx) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.setContentType("application/json;charset=UTF-8");
                                    String body = """
                            {"timestamp":"%s","path":"%s","status":401,"code":"UNAUTHORIZED","message":"인증이 필요합니다.","errors":[]}
                            """.formatted(LocalDateTime.now(), request.getRequestURI());
                                    response.getWriter().write(body);
                                },
                                apiMatcher
                        )
                        .accessDeniedHandler((request, response, deniedEx) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            String body = """
                        {"timestamp":"%s","path":"%s","status":403,"code":"ACCESS_DENIED","message":"접근 권한이 없습니다.","errors":[]}
                        """.formatted(LocalDateTime.now(), request.getRequestURI());
                            response.getWriter().write(body);
                        })
                )

                // 폼 로그인
                .formLogin(form -> form
                        // .loginPage("/login") // 커스텀 페이지 사용 시
                        .successHandler(new LoginSuccessHandler())
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}