package com.sopo.domain.delivery;

import com.sopo.common.BaseEntity;
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

    @Column(nullable = false, length = 10)
    private String receiverName;

    @Column(nullable = false, length = 20)
    private String receiverPhone;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "address_id")
    private Address address;

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