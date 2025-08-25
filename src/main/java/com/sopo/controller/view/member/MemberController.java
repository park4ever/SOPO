package com.sopo.controller.view.member;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.member.request.MemberSignupRequest;
import com.sopo.dto.member.request.MemberUpdateRequest;
import com.sopo.dto.member.request.PasswordChangeRequest;
import com.sopo.dto.member.response.MemberResponse;
import com.sopo.security.session.MemberSession;
import com.sopo.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signup", new MemberSignupRequest("", "", "", ""));

        return "member/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("signup") MemberSignupRequest req,
                         BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            return "member/signup";
        }
        memberService.register(req);
        ra.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인 해주세요.");

        return "redirect:/login";
    }

    @GetMapping("/me")
    public String me(@LoginMember MemberSession session, Model model) {
        MemberResponse me = memberService.getMe(session.id());
        model.addAttribute("me", me);
        //수정 폼 바인딩용 기본 값
        model.addAttribute("profile", new MemberUpdateRequest(me.name(), me.phoneNumber()));
        model.addAttribute("pwChange", new PasswordChangeRequest("", ""));

        return "member/me";
    }

    @PostMapping("/me/update")
    public String updateMe(@LoginMember MemberSession session,
                           @Valid @ModelAttribute("profile") MemberUpdateRequest req,
                           BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "입력값을 확인해 주세요.");
            return "redirect:/members/me";
        }
        memberService.updateMe(session.id(), req);
        ra.addFlashAttribute("message", "프로필이 수정되었습니다.");

        return "redirect:/members/me";
    }

    @PostMapping("/me/password")
    public String changePassword(@LoginMember MemberSession session,
                                 @Valid @ModelAttribute("pwChange") PasswordChangeRequest req,
                                 BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "비밀번호 형식을 확인해 주세요.");
            return "redirect:/members/me";
        }
        memberService.changePassword(session.id(), req);
        ra.addFlashAttribute("message", "비밀번호가 변경되었습니다.");

        return "redirect:/members/me";
    }

    @PostMapping("/me/withdraw")
    public String withdraw(@LoginMember MemberSession session,
                           @RequestParam("confirmPassword") String confirmPassword,
                           RedirectAttributes ra) {
        memberService.withdraw(session.id(), confirmPassword);
        ra.addFlashAttribute("message", "탈퇴가 완료되었습니다.");
        //TODO 세션 무효화는 별도 Logout 처리 또는 필터/핸들러에서 처리
        return "redirect:/";
    }
}