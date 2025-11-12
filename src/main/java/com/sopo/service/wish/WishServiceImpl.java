package com.sopo.service.wish;

import com.sopo.domain.item.Item;
import com.sopo.domain.member.Member;
import com.sopo.domain.personalization.wish.Wish;
import com.sopo.dto.wish.request.WishCreateRequest;
import com.sopo.dto.wish.response.WishResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.item.ItemRepository;
import com.sopo.repository.member.MemberRepository;
import com.sopo.repository.personalization.WishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishServiceImpl implements WishService {

    private final WishRepository wishRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Long add(Long memberId, WishCreateRequest request) {
        Long itemId = request.itemId();

        //상품 존재 검증
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));

        //중복 방지
        if (wishRepository.existsByMemberIdAndItemId(memberId, itemId)) {
            throw new BusinessException(ErrorCode.WISH_ALREADY_EXISTS);
        }

        //프록시 참조로 불필요한 로딩 회피
        Member memberRef = memberRepository.getReferenceById(memberId);

        Wish wish = Wish.create(memberRef, item);
        wishRepository.save(wish);

        return wish.getId();
    }

    @Override
    @Transactional
    public void remove(Long memberId, Long wishId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WISH_NOT_FOUND));

        if (!wish.isOwner(memberId)) {
            throw new BusinessException(ErrorCode.WISH_FORBIDDEN_ACCESS);
        }
        wishRepository.delete(wish);
    }

    @Override
    @Transactional
    public void removeByItem(Long memberId, Long itemId) {
        wishRepository.deleteByMemberIdAndItemId(memberId, itemId);
    }

    @Override
    public List<WishResponse> listMine(Long memberId) {
        //페이징 미도입(추후 결정) -> 전량 조회(최신순 정렬)
        var page = wishRepository.findByMemberId(memberId, Pageable.unpaged());

        return page.getContent().stream()
                .sorted(Comparator.comparing(Wish::getCreatedDate).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Override
    public boolean exists(Long memberId, Long itemId) {
        return wishRepository.existsByMemberIdAndItemId(memberId, itemId);
    }

    @Override
    public long countForItem(Long itemId) {
        return wishRepository.countByItemId(itemId);
    }

    @Override
    @Transactional
    public boolean toggle(Long memberId, Long itemId) {
        if (wishRepository.existsByMemberIdAndItemId(memberId, itemId)) {
            wishRepository.deleteByMemberIdAndItemId(memberId, itemId);
            return false; //삭제됨
        }
        try {
            Member memberRef = memberRepository.getReferenceById(memberId);
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
            wishRepository.save(Wish.create(memberRef, item));
            return true;  //추가됨
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            //경쟁으로 인해 이미 추가된 경우 안전하게 삭제로 맞추거나, false 반환
            return false;
        }
    }

    private WishResponse toResponse(Wish wish) {
        Item item = wish.getItem();

        return new WishResponse(
                wish.getId(),
                item.getId(),
                item.getName(),
                item.getBrand(),
                item.getPrice(),
                item.getStatus(),
                item.getThumbnailUrlOrNull(),
                wish.getCreatedDate()
        );
    }
}