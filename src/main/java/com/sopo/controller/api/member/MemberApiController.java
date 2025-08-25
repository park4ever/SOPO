package com.sopo.controller.api.member;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.member.request.MemberSignupRequest;
import com.sopo.dto.member.request.MemberSignupWithAddressRequest;
import com.sopo.dto.member.request.MemberUpdateRequest;
import com.sopo.dto.member.request.PasswordChangeRequest;
import com.sopo.dto.member.response.MemberResponse;
import com.sopo.facade.member.MemberRegistrationFacade;
import com.sopo.security.session.MemberSession;
import com.sopo.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberApiController {

    private final MemberService memberService;
    private final MemberRegistrationFacade memberRegistrationFacade;

    @PostMapping
    public ResponseEntity<Void> register(@Valid @RequestBody MemberSignupRequest req) {
        Long memberId = memberService.register(req);

        return ResponseEntity.created(URI.create("/api/members/" + memberId)).build();
    }

    @PostMapping("/with-address")
    public ResponseEntity<Void> registerWithAddress(@Valid @RequestBody MemberSignupWithAddressRequest req) {
        Long memberId = memberRegistrationFacade.registerWithAddress(req);

        return ResponseEntity.created(URI.create("/api/members/" + memberId)).build();
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMe(@LoginMember MemberSession session) {
        MemberResponse me = memberService.getMe(session.id());

        return ResponseEntity.ok(me);
    }

    @PatchMapping("/me")
    public ResponseEntity<Void> updateMe(@LoginMember MemberSession session,
                                         @Valid @RequestBody MemberUpdateRequest req) {
        memberService.updateMe(session.id(), req);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(@LoginMember MemberSession session,
                                               @Valid @RequestBody PasswordChangeRequest req) {
        memberService.changePassword(session.id(), req);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(@LoginMember MemberSession session,
                                         @RequestParam("confirmPassword") String confirmPassword) {
        memberService.withdraw(session.id(), confirmPassword);

        return ResponseEntity.noContent().build();
    }
}