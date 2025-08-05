package com.sopo.domain;

import com.sopo.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = PROTECTED)
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, name = "is_enabled")
    private boolean isEnabled;

    private Member(String email, String password, String nickname, String phoneNumber, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isEnabled = true;
    }

    public static Member create(String email, String encryptedPassword, String nickname, String phoneNumber, Role role) {
        return new Member(email, encryptedPassword, nickname, phoneNumber, role);
    }

    public void addAddress(Address address) {
        addresses.add(address);
        if (address.getMember() != this) {
            address.assignMember(this);
        }
    }

    public void changePassword(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 비어 있을 수 없습니다.");
        }
        this.password = encodedPassword;
    }

    public void disable() {
        this.isEnabled = false;
    }
}