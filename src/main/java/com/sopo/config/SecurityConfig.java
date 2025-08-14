package com.sopo.config;

import com.sopo.config.auth.LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //세션 기반
                .securityContext(Customizer.withDefaults())
                .sessionManagement(Customizer.withDefaults())

                // CSRF: Thymeleaf 폼 사용 대비 기본 켜둠(커스텀 API 생기면 필요에 따라 제외 경로 추가)
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/api/**" // 추후 REST API에 대해서만 예외를 두는 식으로 조정
                ))

                //인가 규칙
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/logout",
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/seller/**").hasAnyRole("SELLER", "ADMIN")
                        .anyRequest().authenticated()
                )

                //폼 로그인 스켈레톤(나중에 커스텀 로그인 컨트롤러로 교체 가능)
                .formLogin(form -> form
                        /*.loginPage("/login")            // Thymeleaf 페이지 만들기 전까지는 /login GET 허용만
                        .loginProcessingUrl("/login")   // POST 처리(Default UsernamePasswordAuthenticationFilter)
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")*/
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}