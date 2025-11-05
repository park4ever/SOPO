package com.sopo.service.delivery;

import com.sopo.dto.delivery.request.DeliveryCreateRequest;
import com.sopo.dto.delivery.request.DeliveryUpdateRequest;
import com.sopo.dto.delivery.response.DeliveryResponse;

public interface DeliveryService {

    /** 로그인 회원(memberId) 본인 주문에 대한 배송정보 생성 */
    Long create(Long memberId, DeliveryCreateRequest request);

    /** 낙관적 락(version) 기반 부분 수정(PATCH 스타일) */
    void update(Long memberId, Long deliveryId, DeliveryUpdateRequest request);
    
    /** 주문 기반 단건 조회(본인 소유만)  */
    DeliveryResponse getByOrderId(Long memberId, Long orderId);
}