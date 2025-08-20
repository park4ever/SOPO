package com.sopo.service.address;

import com.sopo.dto.address.request.AddressCreateRequest;
import com.sopo.dto.address.request.AddressUpdateRequest;
import com.sopo.dto.address.response.AddressResponse;

import java.util.List;

public interface AddressService {

    Long add(Long memberId, AddressCreateRequest req);
    void update(Long memberId, Long addressId, AddressUpdateRequest req);
    void remove(Long memberId, Long addressId);
    void setDefault(Long memberId, Long addressId);
    AddressResponse get(Long memberId, Long addressId);
    List<AddressResponse> list(Long memberId);
}