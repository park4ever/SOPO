package com.sopo.service.delivery;

import com.sopo.domain.address.Address;
import com.sopo.domain.delivery.Delivery;
import com.sopo.domain.order.Order;
import com.sopo.domain.order.OrderStatus;
import com.sopo.dto.delivery.request.DeliveryCreateRequest;
import com.sopo.dto.delivery.request.DeliveryUpdateRequest;
import com.sopo.dto.delivery.response.DeliveryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.address.AddressRepository;
import com.sopo.repository.delivery.DeliveryRepository;
import com.sopo.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sopo.domain.order.OrderStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;

    @Override
    public Long create(Long memberId, DeliveryCreateRequest request) {
        Order order = orderRepository.findByIdAndMemberId(request.orderId(), memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!isCreatable(order.getStatus())) {
            throw new BusinessException(ErrorCode.DELIVERY_UPDATE_NOT_ALLOWED);
        }

        if (deliveryRepository.existsByOrderId(order.getId())) {
            throw new BusinessException(ErrorCode.DELIVERY_ALREADY_EXISTS);
        }

        Address address = addressRepository.findByIdAndMemberId(request.addressId(), memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        Delivery delivery = Delivery.create(request.receiverName(), request.receiverPhone(), address, order);
        deliveryRepository.save(delivery);

        return delivery.getId();
    }

    @Override
    public void update(Long memberId, Long deliveryId, DeliveryUpdateRequest request) {
        Delivery delivery = deliveryRepository.findByIdAndOrderMemberId(deliveryId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DELIVERY_NOT_FOUND));

        if (!delivery.getVersion().equals(request.version())) {
            throw new BusinessException(ErrorCode.OPTIMISTIC_LOCK_CONFLICT);
        }

        OrderStatus status = delivery.getOrder().getStatus();
        if (!isUpdatable(status)) {
            throw new BusinessException(ErrorCode.DELIVERY_UPDATE_NOT_ALLOWED);
        }

        if (request.receiverName() != null
                && !request.receiverName().equals(delivery.getReceiverName())) {
            delivery.changeReceiver(request.receiverName());
        }

        if (request.receiverPhone() != null
                && !request.receiverPhone().equals(delivery.getReceiverPhone())) {
            delivery.changePhone(request.receiverPhone());
        }

        if (request.addressId() != null
                && !request.addressId().equals(delivery.getAddressId())) {
            Address address = addressRepository.findByIdAndMemberId(request.addressId(), memberId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));
            delivery.changeAddress(address);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryResponse getByOrderId(Long memberId, Long orderId) {
        if (orderRepository.findByIdAndMemberId(orderId, memberId).isEmpty()) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DELIVERY_NOT_FOUND));

        return toResponse(delivery);
    }

    private boolean isCreatable(OrderStatus status) {
        return status == PAID || status == PREPARING;
    }

    private boolean isUpdatable(OrderStatus status) {
        return status == ORDERED || status == PAID || status == PREPARING;
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        return new DeliveryResponse(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getAddressId(),
                delivery.getReceiverName(),
                delivery.getReceiverPhone(),
                delivery.getVersion(),
                delivery.getCreatedDate(),
                delivery.getLastModifiedDate()
        );
    }
}