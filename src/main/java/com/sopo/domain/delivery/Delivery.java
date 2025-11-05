package com.sopo.domain.delivery;

import com.sopo.domain.common.BaseEntity;
import com.sopo.domain.address.Address;
import com.sopo.domain.order.Order;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
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

    @Column(nullable = false, length = 20)
    private String receiverPhone;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Version
    private Long version;

    private Delivery(String receiverName, String receiverPhone, Address address, Order order) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.address = address;
        this.order = order;
    }

    public static Delivery create(String receiverName, String receiverPhone, Address address, Order order) {
        return new Delivery(receiverName, receiverPhone, address, order);
    }

    public void changeReceiver(String newReceiverName) {
        if (newReceiverName == null || newReceiverName.isBlank()) {
            throw new BusinessException(ErrorCode.DELIVERY_INVALID_RECEIVER_NAME);
        }
        this.receiverPhone = newReceiverName;
    }

    public void changePhone(String newReceiverPhone) {
        if (newReceiverPhone == null || newReceiverPhone.isBlank()) {
            throw new BusinessException(ErrorCode.DELIVERY_INVALID_RECEIVER_PHONE);
        }
        this.receiverPhone = newReceiverPhone;
    }

    public void changeAddress(Address newAddress) {
        if (newAddress == null) {
            throw new BusinessException(ErrorCode.DELIVERY_ADDRESS_REQUIRED);
        }
        this.address = newAddress;
    }

    public Long getOrderId() {
        return (order != null) ? order.getId() : null;
    }

    public Long getAddressId() {
        return (address != null) ? address.getId() : null;
    }
}