package com.sopo.controller.view.order.admin;

import com.sopo.domain.order.OrderStatus;
import com.sopo.repository.order.cond.OrderQueryCond;
import com.sopo.security.aop.AdminOnly;
import com.sopo.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
@AdminOnly
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String list(OrderQueryCond cond, Pageable pageable, Model model) {
        model.addAttribute("page", orderService.searchOrdersAsAdmin(cond, pageable));
        model.addAttribute("cond", cond);
        return "admin/order/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("order", orderService.getByIdAsAdmin(id));
        return "admin/order/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("target") OrderStatus target,
                               RedirectAttributes ra) {
        if (target == null) {
            ra.addFlashAttribute("error", "변경할 상태를 선택하세요.");
            return "redirect:/admin/orders/{id}";
        }
        orderService.updateStatusAsAdmin(id, target);
        ra.addFlashAttribute("message", "주문 상태가 변경되었습니다.");
        return "redirect:/admin/orders/{id}";
    }
}