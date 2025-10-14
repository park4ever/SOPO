package com.sopo.controller.view.order;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.order.request.OrderCreateRequest;
import com.sopo.dto.order.response.OrderSummaryResponse;
import com.sopo.repository.order.cond.OrderQueryCond;
import com.sopo.security.session.MemberSession;
import com.sopo.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public String list(@LoginMember MemberSession session,
                       OrderQueryCond cond, Pageable pageable, Model model) {
        Page<OrderSummaryResponse> page = orderService.searchMyOrders(session.id(), cond, pageable);
        model.addAttribute("page", page);
        model.addAttribute("cond", cond);
        return "order/list";
    }

    @GetMapping("/{id}")
    public String detail(@LoginMember MemberSession session,
                         @PathVariable("id") Long orderId,
                         Model model) {
        model.addAttribute("order", orderService.getMyOrder(session.id(), orderId));
        return "order/detail";
    }

    @PostMapping
    public String create(@LoginMember MemberSession session,
                         @ModelAttribute("form") @Valid OrderCreateRequest form,
                         BindingResult bindingResult,
                         RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "입력값을 확인해주세요.");
            return "redirect:/orders";
        }
        Long orderId = orderService.create(session.id(), form);
        ra.addFlashAttribute("message", "주문이 생성되었습니다. (#" + orderId + ")");
        return "redirect:/orders/" + orderId;
    }
}