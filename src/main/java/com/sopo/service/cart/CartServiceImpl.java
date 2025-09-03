package com.sopo.service.cart;

import com.sopo.common.money.Money;
import com.sopo.domain.cart.Cart;
import com.sopo.domain.cart.CartItem;
import com.sopo.domain.item.*;
import com.sopo.dto.cart.request.CartItemAddRequest;
import com.sopo.dto.cart.request.CartItemUpdateQuantityRequest;
import com.sopo.dto.cart.response.CartItemResponse;
import com.sopo.dto.cart.response.CartSummaryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.cart.CartItemRepository;
import com.sopo.repository.cart.CartRepository;
import com.sopo.repository.item.ItemOptionRepository;
import com.sopo.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public CartSummaryResponse getMyCart(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));

        List<CartItem> items = cartItemRepository.findWithItemGraphByCartIdOnlyThumbnail(cart.getId());

        List<CartItemResponse> lines = items.stream()
                .map(this::toResponse)
                .collect(toList());

        int totalQty = lines.stream().mapToInt(CartItemResponse::quantity).sum();
        BigDecimal subtotal = lines.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean hasUnavailable = lines.stream().anyMatch(l -> !l.available());

        return new CartSummaryResponse(
                cart.getId(),
                memberId,
                lines,
                totalQty,
                subtotal,
                hasUnavailable
        );
    }

    @Override
    public Long addItem(Long memberId, CartItemAddRequest request) {
        if (request.quantity() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }

        //장바구니가 없다면 첫 담기 시 생성
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> {
                    try {
                        return cartRepository.save(Cart.create(memberRepository.getReferenceById(memberId)));
                    } catch (DataIntegrityViolationException dup) {
                        // 동시에 다른 트랜잭션이 먼저 만들었음 → 재조회로 수습
                        return cartRepository.findByMemberId(memberId)
                                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));
                    }
                });


        //옵션 및 상품 상태 검증
        ItemOption option = itemOptionRepository.findById(request.itemOptionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
        Item item = option.getItem();
        validatePurchasable(item, option, request.quantity());

        //동일 옵션 존재 -> 수량 가산, 없으면 신규(유니크 충돌 재시도 포함)
        return cartItemRepository.findByCartIdAndItemOptionId(cart.getId(), option.getId())
                .map(existing -> {
                    int newQty = existing.getQuantity() + request.quantity();
                    ensureStock(option, newQty);
                    existing.changeQuantity(newQty);
                    return existing.getId();
                })
                .orElseGet(() -> {
                    CartItem ci = CartItem.create(option, request.quantity());
                    ci.assignCart(cart);
                    try {
                        return cartItemRepository.save(ci).getId();
                    } catch (DataIntegrityViolationException dup) {
                        //(cart_id, item_option_id) 유니크 충돌 -> 재조회 병합
                        CartItem exists = cartItemRepository
                                .findByCartIdAndItemOptionId(cart.getId(), option.getId())
                                .orElseThrow(() -> new BusinessException(ErrorCode.DUPLICATED_CART_ITEM));
                        int newQty = exists.getQuantity() + request.quantity();
                        ensureStock(option, newQty);
                        exists.changeQuantity(newQty);
                        return exists.getId();
                    }
                });
    }

    @Override
    public void updateQuantity(Long memberId, CartItemUpdateQuantityRequest request) {
        if (request.quantity() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }

        CartItem ci = cartItemRepository.findOwnedWithItem(request.cartItemId(), memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        ItemOption option = ci.getItemOption();
        Item item = option.getItem();
        validatePurchasable(item, option, request.quantity());
        ci.changeQuantity(request.quantity());
    }

    @Override
    public void removeItem(Long memberId, Long cartItemId) {
        CartItem ci = cartItemRepository.findOwnedWithItem(cartItemId, memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));
        cartItemRepository.delete(ci);
    }

    @Override
    public void clear(Long memberId) {
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));
        cartItemRepository.deleteAllByCartId(cart.getId());
    }

    private void validatePurchasable(Item item, ItemOption option, int qty) {
        if (item == null) throw new BusinessException(ErrorCode.ITEM_NOT_FOUND);
        if (item.isDeleted()) throw new BusinessException(ErrorCode.ITEM_DELETED);
        if (item.getStatus() != ItemStatus.ON_SALE) throw new BusinessException(ErrorCode.ITEM_NOT_ON_SALE);
        if (option.isSoldOut()) throw new BusinessException(ErrorCode.OPTION_SOLD_OUT);
        ensureStock(option, qty);
    }

    private void ensureStock(ItemOption option, int qty) {
        if (option.getStock() < qty) throw new BusinessException(ErrorCode.QUANTITY_EXCEEDS_STOCK);
    }

    private CartItemResponse toResponse(CartItem ci) {
        ItemOption opt = ci.getItemOption();
        Item item = opt.getItem();

        Money unit = Money.of(item.getPrice());
        Money line = unit.times(ci.getQuantity());

        String thumbnailUrl = item.getImages().stream()
                .filter(ItemImage::isThumbnail)
                .findFirst()
                .map(ItemImage::getImageUrl)
                .orElse(null);

        UnavailableReason reason = null;

        if (item.isDeleted()) {
            reason = UnavailableReason.ITEM_DELETED;
        } else if (item.getStatus() != ItemStatus.ON_SALE) {
            reason = UnavailableReason.ITEM_NOT_ON_SALE;
        } else if (opt.isSoldOut()) {
            reason = UnavailableReason.OPTION_SOLD_OUT;
        } else if (opt.getStock() < ci.getQuantity()) {
            reason = UnavailableReason.QUANTITY_EXCEEDS_STOCK;
        }

        boolean available = (reason == null);
        Integer maxPurchasable = available ? opt.getStock() : null;

        return new CartItemResponse(
                ci.getId(),
                item.getId(),
                item.getName(),
                item.getBrand(),
                opt.getId(),
                opt.getColor().getName(),
                opt.getSize().getName(),
                unit.asBigDecimal(),
                ci.getQuantity(),
                line.asBigDecimal(),
                available,
                maxPurchasable,
                thumbnailUrl,
                reason
        );
    }
}