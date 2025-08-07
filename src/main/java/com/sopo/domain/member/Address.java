package com.sopo.domain.member;

import com.sopo.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "address")
@NoArgsConstructor(access = PROTECTED)
public class Address extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 200)
    private String roadAddress; //도로명 주소

    @Column(length = 200)
    private String landAddress;    //지번 주소

    @Column(length = 100)
    private String detailAddress;   //상세 주소 (아파트/동/호수 등)

    @Column(length = 20)
    private String zipcode; //우편번호 (5자리)

    @Column(nullable = false, name = "is_default")
    private boolean isDefault;  //기본 배송지 여부

    private Address(String roadAddress, String landAddress, String detailAddress, String zipcode, boolean isDefault) {
        this.roadAddress = roadAddress;
        this.landAddress = landAddress;
        this.detailAddress = detailAddress;
        this.zipcode = zipcode;
        this.isDefault = isDefault;
    }

    public void assignMember(Member member) {
        if (this.member != member) {
            this.member = member;
            member.addAddress(this); //연관관계 역방향도 안전하게 처리 (중복 방지)
        }
    }

    public static Address create(Member member, String roadAddress, String landAddress, String detailAddress, String zipcode, boolean isDefault) {
        Address address = new Address(roadAddress, landAddress, detailAddress, zipcode, isDefault);
        address.assignMember(member);
        return address;
    }

    public void markAsDefault() {
        this.isDefault = true;
    }

    public void unsetDefault() {
        this.isDefault = false;
    }
}