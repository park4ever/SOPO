package com.sopo.facade.member;

import com.sopo.dto.member.request.MemberSignupWithAddressRequest;
import com.sopo.service.address.AddressService;
import com.sopo.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Transactional
@Validated
public class MemberRegistrationFacadeImpl implements MemberRegistrationFacade {

    private final MemberService memberService;
    private final AddressService addressService;

    @Override
    public Long registerWithAddress(@Valid MemberSignupWithAddressRequest req) {
        //회원 가입
        Long memberId = memberService.register(req.member());

        //주소 생성
        addressService.add(memberId, req.address());

        return memberId;
    }
}