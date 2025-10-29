package com.sopo.controller.view.coupon.admin;

import com.sopo.dto.coupon.request.CreateFixedCouponRequest;
import com.sopo.dto.coupon.request.CreateRateCouponRequest;
import com.sopo.dto.coupon.request.PreviewCouponRequest;
import com.sopo.repository.coupon.cond.CouponQueryCond;
import com.sopo.security.aop.AdminOnly;
import com.sopo.service.coupon.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
@AdminOnly
public class AdminCouponController {

    private final CouponService couponService;

    @GetMapping
    public String list(CouponQueryCond cond, Pageable pageable, Model model) {
        model.addAttribute("page", couponService.search(cond, pageable, LocalDateTime.now()));
        model.addAttribute("cond", cond);
        return "admin/coupon/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("coupon", couponService.get(id));
        return "admin/coupon/detail";
    }

    @GetMapping("/new-fixed")
    public String newFixedForm(Model model) {
        model.addAttribute("form", new CreateFixedCouponRequest(null, null, null, null, null));
        return "admin/coupon/new-fixed";
    }

    @PostMapping("/new-fixed")
    public String createFixed(@ModelAttribute("form") @Valid CreateFixedCouponRequest form,
                              BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "입력값을 확인해주세요.");
            return "redirect:/admin/coupons/new-fixed";
        }
        Long id = couponService.createFixed(form);
        ra.addFlashAttribute("message", "정액 쿠폰이 생성되었습니다. (#" + id + ")");
        return "redirect:/admin/coupons/" + id;
    }

    @GetMapping("/new-rate")
    public String newRateForm(Model model) {
        model.addAttribute("form", new CreateRateCouponRequest(null, null, null, null, null, null));
        return "admin/coupon/new-rate";
    }

    @PostMapping("/new-rate")
    public String createRate(@ModelAttribute("form") @Valid CreateRateCouponRequest form,
                             BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "입력값을 확인해주세요.");
            return "redirect:/admin/coupons/new-rate";
        }
        Long id = couponService.createRate(form);
        ra.addFlashAttribute("message", "정률 쿠폰이 생성되었습니다. (#" + id + ")");
        return "redirect:/admin/coupons/" + id;
    }

    @PostMapping("/{id}/preview")
    public String preview(@PathVariable("id") Long id,
                          @ModelAttribute("form") @Valid PreviewCouponRequest form,
                          BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "주문 금액을 확인해주세요.");
            return "redirect:/admin/coupons/{id}";
        }
        var discount = couponService.preview(id, form, LocalDateTime.now());
        ra.addFlashAttribute("message", "예상 할인액 : " + discount);
        return "redirect:/admin/coupons/{id}";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, RedirectAttributes ra) {
        couponService.softDelete(id);
        ra.addFlashAttribute("message", "쿠폰이 삭제(비활성화) 처리되었습니다.");
        return "redirect:/admin/coupons";
    }
}