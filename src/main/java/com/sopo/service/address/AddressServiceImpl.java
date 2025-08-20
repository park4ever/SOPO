package com.sopo.service.address;

import com.sopo.domain.address.Address;
import com.sopo.domain.member.Member;
import com.sopo.dto.address.request.AddressCreateRequest;
import com.sopo.dto.address.request.AddressUpdateRequest;
import com.sopo.dto.address.response.AddressResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.address.AddressRepository;
import com.sopo.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sopo.exception.ErrorCode.*;
import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;

    @Override
    public Long add(Long memberId, AddressCreateRequest req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));

        boolean hasDefault = addressRepository.existsByMemberIdAndIsDefaultTrue(memberId);

        boolean makeDefault = req.isDefault() || !hasDefault;

        if (makeDefault) {
            addressRepository.clearDefaultByMemberId(memberId);
        }

        Address address = Address.create(
                member,
                req.roadAddress(),
                req.landAddress(),
                req.detailAddress(),
                req.zipcode(),
                makeDefault
        );

        return addressRepository.save(address).getId();
    }

    @Override
    public void update(Long memberId, Long addressId, AddressUpdateRequest req) {
        Address address = addressRepository.findByIdAndMemberId(addressId, memberId)
                .orElseThrow(() -> new BusinessException(ADDRESS_NOT_FOUND));

        address.change(req.roadAddress(), req.landAddress(), req.detailAddress(), req.zipcode());
    }

    @Override
    public void remove(Long memberId, Long addressId) {
        Address address = addressRepository.findByIdAndMemberId(addressId, memberId)
                .orElseThrow(() -> new BusinessException(ADDRESS_NOT_FOUND));

        boolean wasDefault = address.isDefault();

        addressRepository.delete(address);

        if (wasDefault) {
            long remain = addressRepository.countByMemberId(memberId);
            if (remain > 0) {
                addressRepository.clearDefaultByMemberId(memberId); //중복방지
                addressRepository.findFirstByMemberIdOrderByIdDesc(memberId)
                        .ifPresent(Address::markAsDefault); //트랜잭션 내 더티체킹 반영
            }
        }
    }

    @Override
    public void setDefault(Long memberId, Long addressId) {
        Address address = addressRepository.findByIdAndMemberId(addressId, memberId)
                .orElseThrow(() -> new BusinessException(ADDRESS_NOT_FOUND));

        addressRepository.clearDefaultByMemberId(memberId);
        address.markAsDefault();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse get(Long memberId, Long addressId) {
        Address address = addressRepository.findByIdAndMemberId(addressId, memberId)
                .orElseThrow(() -> new BusinessException(ADDRESS_NOT_FOUND));

        return toResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> list(Long memberId) {
        return addressRepository.findAllByMemberIdOrderByIsDefaultDescIdDesc(memberId)
                .stream().map(this::toResponse)
                .collect(toList());
    }

    private AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getRoadAddress(),
                address.getLandAddress(),
                address.getDetailAddress(),
                address.getZipcode(),
                address.isDefault()
        );
    }
}