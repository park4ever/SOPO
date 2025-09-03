package com.sopo.controller.view.cart;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.cart.request.CartItemAddRequest;
import com.sopo.dto.cart.request.CartItemUpdateQuantityRequest;
import com.sopo.dto.cart.response.CartSummaryResponse;
import com.sopo.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String view(@LoginMember Long memberId, Model model) {
        CartSummaryResponse cart = cartService.getMyCart(memberId);
        model.addAttribute("cart", cart);

        //신규 담기 폼 초기값(뷰에서 th:object 사용)
        model.addAttribute("addForm", new CartItemAddRequest(null, null, 1));
        return "cart/cart";
    }

    @PostMapping("/items")
    public String add(@LoginMember Long memberId,
                      @ModelAttribute("addForm") @Valid CartItemAddRequest form,
                      BindingResult bindingResult, RedirectAttributes ra) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "입력값을 확인해주세요.");
            return "redirect:/cart";
        }

        var req = new CartItemAddRequest(form.itemId(), form.itemOptionId(), form.quantity());
        Long cartItemId = cartService.addItem(memberId, req);
        ra.addFlashAttribute("message", "장바구니에 상품을 담았습니다. (#" + cartItemId + ")");
        return "redirect:/cart";
    }

    @PostMapping("/items/{cartItemId}/quantity")
    public String updateQuantity(@LoginMember Long memberId,
                                 @PathVariable("cartItemId") Long cartItemId,
                                 @RequestParam("quantity") Integer quantity,
                                 RedirectAttributes ra) {
        if (quantity == null || quantity <= 0) {
            ra.addFlashAttribute("error", "수량은 1 이상이어야 합니다.");
            return "redirect:/cart";
        }

        var req = new CartItemUpdateQuantityRequest(cartItemId, quantity);
        cartService.updateQuantity(memberId, req);
        ra.addFlashAttribute("message", "수량이 변경되었습니다.");
        return "redirect:/cart";
    }

    @PostMapping("/items/{cartItemId}/remove")
    public String remove(@LoginMember Long memberId,
                         @PathVariable Long cartItemId,
                         RedirectAttributes ra) {
        cartService.removeItem(memberId, cartItemId);
        ra.addFlashAttribute("message", "장바구니 항목을 삭제했습니다.");
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clear(@LoginMember Long memberId, RedirectAttributes ra) {
        cartService.clear(memberId);
        ra.addFlashAttribute("message", "장바구니를 비웠습니다.");
        return "redirect:/cart";
    }
}