package com.sopo.controller.view.coupon;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.coupon.request.PreviewMemberCouponRequest;
import com.sopo.dto.coupon.request.UseMemberCouponRequest;
import com.sopo.dto.coupon.response.MemberCouponRowResponse;
import com.sopo.repository.memberCoupon.cond.MemberCouponQueryCond;
import com.sopo.security.session.MemberSession;
import com.sopo.service.coupon.MemberCouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member/coupons")
public class MemberCouponController {

    private final MemberCouponService memberCouponService;

    @GetMapping
    public String list(@LoginMember MemberSession session,
                       MemberCouponQueryCond cond, Pageable pageable, Model model) {
        Page<MemberCouponRowResponse> page =
                memberCouponService.searchMy(session.id(), cond, pageable, LocalDateTime.now());
        model.addAttribute("page", page);
        model.addAttribute("cond", cond);
        return "member/coupon/list";
    }

    @PostMapping("/{id}/preview")
    public String preview(@LoginMember MemberSession session,
                          @PathVariable("id") Long memberCouponId,
                          @ModelAttribute("form") @Valid PreviewMemberCouponRequest form,
                          BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "주문 금액을 확인해주세요.");
            return "redirect:/member/coupons";
        }
        BigDecimal discount =
                memberCouponService.preview(memberCouponId, session.id(), form, LocalDateTime.now());
        ra.addFlashAttribute("message", "예상 할인액 : " + discount);
        return "redirect:/member/coupons";
    }

    @PostMapping("/{id}/use")
    public String use(@LoginMember MemberSession session,
                      @PathVariable("id") Long memberCouponId,
                      @ModelAttribute("form") @Valid UseMemberCouponRequest form,
                      BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "입력값을 확인해주세요.");
            return "redirect:/member/coupons";
        }
        //memberId와 memberCouponId는 서버가 주입(클라이언트 값 무시)
        UseMemberCouponRequest effective = new UseMemberCouponRequest(
                memberCouponId,
                session.id(),
                form.orderId(),
                form.orderPrice()
        );
        memberCouponService.use(effective, LocalDateTime.now());
        ra.addFlashAttribute("message", "쿠폰이 주문에 적용되었습니다.");
        return "redirect:/orders/" + form.orderId();
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@LoginMember MemberSession session,
                         @PathVariable("id") Long memberCouponId,
                         @RequestParam("orderId") Long orderId,
                         RedirectAttributes ra) {
        memberCouponService.cancelUse(memberCouponId, session.id(), LocalDateTime.now());
        ra.addFlashAttribute("message", "쿠폰 사용이 취소되었습니다.");
        return "redirect:/orders/" + orderId;
    }
}