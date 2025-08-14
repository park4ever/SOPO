package com.sopo.service.member;

import com.sopo.dto.member.response.MemberResponse;
import com.sopo.dto.member.request.MemberSignupRequest;
import com.sopo.dto.member.request.MemberUpdateRequest;
import com.sopo.dto.member.request.PasswordChangeRequest;

public interface MemberService {

    Long register(MemberSignupRequest req);
    MemberResponse getMe(Long memberId);
    void updateMe(Long memberId, MemberUpdateRequest req);
    void changePassword(Long memberId, PasswordChangeRequest req);
    void withdraw(Long memberId, String confirmPassword);
}
