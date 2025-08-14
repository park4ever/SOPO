package com.sopo.config.local;

import com.sopo.domain.member.Member;
import com.sopo.domain.member.Role;
import com.sopo.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("local")
@RequiredArgsConstructor
public class LocalTestDataConfig {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initTestMembers() {
        return args -> {
            memberRepository.findByEmail("user@sopo.com").orElseGet(() ->
                    memberRepository.save(Member.create(
                            "user@sopo.com",
                            passwordEncoder.encode("1234"),
                            "홍길동",
                            "010-1234-5678",
                            Role.USER
                    ))
            );

            memberRepository.findByEmail("admin@sopo.com").orElseGet(() ->
                    memberRepository.save(Member.create(
                            "admin@sopo.com",
                            passwordEncoder.encode("1234"),
                            "관리자",
                            "010-3389-2139",
                            Role.ADMIN
                    ))
            );
        };
    }
}