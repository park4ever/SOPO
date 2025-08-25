package com.sopo.facade.member;

import com.sopo.dto.member.request.MemberSignupWithAddressRequest;

public interface MemberRegistrationFacade {
    Long registerWithAddress(MemberSignupWithAddressRequest req);
}