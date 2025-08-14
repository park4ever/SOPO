package com.sopo.service.member;

import com.sopo.common.TextNormalizer;
import com.sopo.domain.member.Member;
import com.sopo.domain.member.Role;
import com.sopo.dto.member.response.MemberResponse;
import com.sopo.dto.member.request.MemberSignupRequest;
import com.sopo.dto.member.request.MemberUpdateRequest;
import com.sopo.dto.member.request.PasswordChangeRequest;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sopo.common.TextNormalizer.normalizePhone;
import static com.sopo.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Long register(MemberSignupRequest req) {
        //정규화
        String normalizedEmail = TextNormalizer.normalizeEmail(req.email());
        String normalizedPhone = normalizePhone(req.phoneNumber());

        //이메일 중복 검사
        if (memberRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessException(EMAIL_DUPLICATED);
        }

        //패스워드 인코딩
        String encoded = passwordEncoder.encode(req.password());

        //Member 생성
        Member member = Member.create(
                normalizedEmail,
                encoded,
                req.name().strip(),
                normalizedPhone,
                Role.USER);

        //저장 (희박한 동시성 충돌 방지)
        try {
            return memberRepository.save(member).getId();
        } catch (DataIntegrityViolationException e) {
            //unique(email) 위반 가능성 -> 동일 코드로 번역
            throw new BusinessException(EMAIL_DUPLICATED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getMe(Long memberId) {
        Member member = getActiveMemberOrThrow(memberId);

        return toResponse(member);
    }

    @Override
    public void updateMe(Long memberId, MemberUpdateRequest req) {
        Member member = getActiveMemberOrThrow(memberId);

        //정규화 + 변경
        String normalizedPhone = normalizePhone(req.phoneNumber());
        String name = req.name().strip();

        member.changeProfile(name, normalizedPhone);
    }

    @Override
    public void changePassword(Long memberId, PasswordChangeRequest req) {
        Member member = getActiveMemberOrThrow(memberId);

        //현재 비밀번호 검증
        if (!passwordEncoder.matches(req.currentPassword(), member.getPassword())) {
            throw new BusinessException(PASSWORD_MISMATCH);
        }

        //새 비밀번호 인코딩 후 저장
        String encoded = passwordEncoder.encode(req.newPassword());
        member.changePassword(encoded);
    }

    @Override
    public void withdraw(Long memberId, String confirmPassword) {
        Member member = getActiveMemberOrThrow(memberId);

        //비밀번호 검증
        if (!passwordEncoder.matches(confirmPassword, member.getPassword())) {
            throw new BusinessException(PASSWORD_MISMATCH);
        }

        //soft-delete
        member.disable();
    }

    private Member getMemberOrThrow(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
    }

    private Member getActiveMemberOrThrow(Long memberId) {
        Member member = getMemberOrThrow(memberId);
        if (!member.isEnabled()) throw new BusinessException(MEMBER_DISABLED);
        return member;
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhoneNumber(),
                member.getRole(),
                member.isEnabled()
        );
    }
}