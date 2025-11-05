package com.sopo.controller.view.delivery;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.delivery.request.DeliveryUpdateRequest;
import com.sopo.dto.delivery.response.DeliveryResponse;
import com.sopo.security.session.MemberSession;
import com.sopo.service.delivery.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order/{orderId}/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    public String view(@LoginMember MemberSession session,
                       @PathVariable("orderId") Long orderId, Model model) {
        DeliveryResponse delivery = deliveryService.getByOrderId(session.id(), orderId);
        model.addAttribute("delivery", delivery);
        return "delivery/detail";
    }

    @GetMapping("/edit")
    public String editForm(@LoginMember MemberSession session,
                           @PathVariable("orderId") Long orderId, Model model) {
        DeliveryResponse delivery = deliveryService.getByOrderId(session.id(), orderId);
        model.addAttribute("delivery", delivery);
        model.addAttribute("form", new DeliveryUpdateRequest(
                delivery.version(),
                delivery.receiverName(),
                delivery.receiverPhone(),
                delivery.addressId()
        ));
        return "delivery/edit";
    }

    @PostMapping("/edit")
    public String edit(@LoginMember MemberSession session,
                       @PathVariable("orderId") Long orderId,
                       @ModelAttribute("form") @Valid DeliveryUpdateRequest form,
                       BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "입력값을 확인해주세요.");
            return "redirect:/orders/{orderId}/delivery/edit";
        }

        // orderId -> deliveryId 역참조
        DeliveryResponse current = deliveryService.getByOrderId(session.id(), orderId);

        // 폼 -> 서비스 DTO
        DeliveryUpdateRequest request = new DeliveryUpdateRequest(
                form.version(), form.receiverName(), form.receiverPhone(), form.addressId()
        );
        deliveryService.update(session.id(), current.id(), request);

        ra.addFlashAttribute("message", "배송 정보가 수정되었습니다.");
        return "redirect:/orders/{orderId}/delivery";
    }
}