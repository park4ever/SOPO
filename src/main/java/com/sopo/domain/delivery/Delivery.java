package com.sopo.domain.delivery;

import com.sopo.domain.common.BaseEntity;
import com.sopo.domain.member.Address;
import com.sopo.domain.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@Table(name = "delivery")
@NoArgsConstructor(access = PROTECTED)
public class Delivery extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String receiverName;

    //TODO 하이픈 제거 등 형식 검증이 필요함 → DTO 계층에서 정규화 및 검증 처리 예정
    @Column(nullable = false, length = 20)
    private String receiverPhone;

    //TODO 배송 도중 주소 변경 요청 등의 유즈케이스는 서비스 계층에서 처리 예정
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "address_id")
    private Address address;

    //TODO 향후 복잡한 물류 흐름이 필요할 경우 DeliveryStatus(enum) 도입 고려
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    private Delivery(String receiverName, String receiverPhone, Address address, Order order) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.address = address;
        this.order = order;
    }

    public static Delivery create(String receiverName, String receiverPhone, Address address, Order order) {
        return new Delivery(receiverName, receiverPhone, address, order);
    }
}