package com.sopo.domain.member;

import com.sopo.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.*;
import static jakarta.persistence.EnumType.*;
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

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false, length = 20)
    private String phoneNumber;

    @OneToMany(mappedBy = "member", cascade = ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    @Enumerated(STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, name = "is_enabled")
    private boolean isEnabled;

    private Member(String email, String password, String name, String phoneNumber, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isEnabled = true;
    }

    public static Member create(String email, String encryptedPassword, String name, String phoneNumber, Role role) {
        return new Member(email, encryptedPassword, name, phoneNumber, role);
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

    public void changeProfile(String name, String phoneNumber) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 비어 있을 수 없습니다.");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("전화번호는 비어 있을 수 없습니다.");
        }
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void disable() {
        this.isEnabled = false;
    }
}