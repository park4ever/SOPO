package com.sopo.service.item;

import com.sopo.domain.item.*;
import com.sopo.domain.member.Member;
import com.sopo.dto.item.request.*;
import com.sopo.dto.item.response.ItemDetailResponse;
import com.sopo.dto.item.response.ItemResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.item.ItemCategoryRepository;
import com.sopo.repository.item.ItemColorRepository;
import com.sopo.repository.item.ItemRepository;
import com.sopo.repository.item.ItemSizeRepository;
import com.sopo.repository.member.MemberRepository;
import com.sopo.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.sopo.domain.item.ItemStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ItemCategoryRepository itemCategoryRepository;
    private final ItemColorRepository itemColorRepository;
    private final ItemSizeRepository itemSizeRepository;
    private final CurrentUserProvider currentUSer;

    @Override
    public Long create(ItemCreateRequest request) {
        validateCreate(request);

        Member seller = memberRepository.findById(request.sellerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        ItemCategory category = null;
        if (request.categoryId() != null) {
            category = itemCategoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        Item item = Item.create(
                request.name(),
                normalizeNullable(request.description()),
                request.price(),
                request.brand(),
                seller,
                category
        );

        return itemRepository.saveAndFlush(item).getId();
    }

    @Override
    public void update(Long itemId, ItemUpdateRequest request) {
        Item item = getActiveItem(itemId);
        assertOwnerOrAdmin(item);

        if (request.name() != null) {
            requireNonBlankMax(request.name(), 50, "name");
            item.changeName(request.name());
        }
        if (request.description() != null) {
            item.changeDescription(request.description());
        }
        if (request.price() != null) {
            requirePositive(request.price(), "price");
            item.changePrice(request.price());
        }
        if (request.brand() != null) {
            requireNonBlankMax(request.brand(), 30, "brand");
            item.changeBrand(request.brand());
        }
        if (request.categoryId() != null) {
            ItemCategory category = itemCategoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
            item.changeCategory(category);
        }
    }

    @Override
    public void changeStatus(Long itemId, ItemStatusChangeRequest request) {
        Item item = getActiveItem(itemId);
        assertOwnerOrAdmin(item);
        item.changeStatus(request.status());
    }

    @Override
    public void softDelete(Long itemId) {
        Item item = getItemOrThrow(itemId);
        assertOwnerOrAdmin(item);
        item.markAsDeleted();
        item.changeStatus(STOPPED);
    }

    @Override
    public void restore(Long itemId) {
        Item item = getItemOrThrow(itemId);
        assertOwnerOrAdmin(item);
        item.unsetDeleted();
        //TODO : 상품 복구 시, 상태 정책은 별도 API로 다루기
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDetailResponse getById(Long itemId) {
        Item item = getActiveItem(itemId);
        return toDetailResponse(item);
    }

    /**
     * TODO : 추후 QueryDSL 커스텀 리포지토리로 치환 예정
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ItemResponse> search(ItemSearchRequest request) {
        Pageable pageable = buildPageable(request);

        // 페이지 데이터 (정렬 포함)
        Page<Item> page = itemRepository.findAll(pageable);
        List<ItemResponse> content = page.getContent().stream()
                .filter(this::notDeleted)
                .filter(it -> filterByKeyword(it, request.keyword()))
                .filter(it -> filterByStatus(it, request.status()))
                .filter(it -> filterByCategory(it, request.categoryId()))
                .map(this::toListResponse)
                .toList();

        // total 계산(임시) — 추후 QueryDSL로 대체
        long total = itemRepository.findAll().stream()
                .filter(this::notDeleted)
                .filter(it -> filterByKeyword(it, request.keyword()))
                .filter(it -> filterByStatus(it, request.status()))
                .filter(it -> filterByCategory(it, request.categoryId()))
                .count();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void increaseSalesVolume(Long itemId, int amount) {
        if (amount <= 0) return;
        Item item = getActiveItem(itemId);
        assertOwnerOrAdmin(item);
        item.increaseSalesVolume(amount);
    }

    @Override
    public void decreaseSalesVolume(Long itemId, int amount) {
        if (amount <= 0) return;
        Item item = getActiveItem(itemId);
        assertOwnerOrAdmin(item);
        item.decreaseSalesVolume(amount);
    }

    @Override
    public Long addImage(Long itemId, ItemImageCreateRequest request) {
        Item item = getActiveItem(itemId);
        assertOwnerOrAdmin(item);

        int sortOrder = (request.sortOrder() != null)
                ? request.sortOrder()
                : item.nextImageSortOrder();

        ItemImage image = ItemImage.create(request.imageUrl(), false, sortOrder);
        image.assignItem(item);
        item.addImage(image);

        if (request.thumbnail()) {
            item.assignThumbnail(image.getId());
        }

        itemRepository.flush(); //IDENTITY + cascade 환경에서 ID 보장을 위한 flush
        return image.getId();
    }

    @Override
    public void removeImage(Long itemId, Long imageId) {
        Item item = getActiveItem(itemId);
        assertOwnerOrAdmin(item);

        ItemImage target = item.findImage(imageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.IMAGE_NOT_FOUND));

        boolean wasThumbnail = target.isThumbnail();
        item.removeImage(imageId);

        if (wasThumbnail) {
            item.getImages().stream()
                    .min(Comparator.comparingInt(ItemImage::getSortOrder))
                    .ifPresent(next -> item.assignThumbnail(next.getId()));
        }
    }

    @Override
    public void reorderImages(Long itemId, ItemImageReorderRequest request) {
        Item item = getActiveItem(itemId);
        assertOwnerOrAdmin(item);

        Map<Long, Integer> orders = request.orders().stream()
                .collect(Collectors.toMap(
                        ItemImageReorderRequest.ImageOrder::imageId,
                        ItemImageReorderRequest.ImageOrder::sortOrder));

        item.reorderImages(orders);
    }

    @Override
    public void assignThumbnail(Long itemId, Long imageId) {
        Item item = getActiveItem(itemId);
        assertOwnerOrAdmin(item);
        //내부에서 존재 검증 + 단일화
        item.assignThumbnail(imageId);
    }

    @Override
    public Long addOption(Long itemId, ItemOptionCreateRequest request) {
        for (int i = 0; i < 3; i++) {
            try {
                Item item = getActiveItem(itemId);
                assertOwnerOrAdmin(item);

                var color = itemColorRepository.findById(request.colorId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.COLOR_NOT_FOUND));
                var size = itemSizeRepository.findById(request.sizeId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.SIZE_NOT_FOUND));

                if (item.hasOption(color, size)) {
                    throw new BusinessException(ErrorCode.DUPLICATE_ITEM_OPTION);
                }

                ItemOption option = ItemOption.create(color, size, request.stock());
                option.assignItem(item);
                item.addOption(option);

                itemRepository.flush();
                return option.getId();
            } catch (ObjectOptimisticLockingFailureException e) {
                if (i == 2) throw e;
            }
        }
        throw new IllegalStateException("예상치 못한 시도입니다. 루프가 종료됩니다.");
    }

    @Override
    public void removeOption(Long itemId, Long optionId) {
        Item item = getActiveItem(itemId);
        assertOwnerOrAdmin(item);

        item.findOption(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
        item.removeOption(optionId);
    }

    @Override
    public void decreaseOptionStock(Long itemId, ItemOptionStockChangeRequest request) {
        for (int i = 0; i < 3; i++) {
            try {
                Item item = getActiveItem(itemId);
                assertOwnerOrAdmin(item);

                ItemOption option = item.findOption(request.optionId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
                option.decreaseStock(request.quantity());
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                if (i == 2) throw e;
            }
        }
    }

    @Override
    public void increaseOptionStock(Long itemId, ItemOptionStockChangeRequest request) {
        for (int i = 0; i < 3; i++) {
            try {
                Item item = getActiveItem(itemId);
                assertOwnerOrAdmin(item);

                ItemOption option = item.findOption(request.optionId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
                option.increaseStock(request.quantity());
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                if (i == 2) throw e;
            }
        }
    }

    @Override
    public void markOptionSoldOut(Long itemId, Long optionId) {
        Item item = getActiveItem(itemId);
        assertOwnerOrAdmin(item);

        ItemOption option = item.findOption(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
        option.markSoldOut();
    }

    @Override
    public void markOptionInStock(Long itemId, Long optionId) {
        Item item = getActiveItem(itemId);
        assertOwnerOrAdmin(item);

        ItemOption option = item.findOption(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
        option.markInStock();
    }

    private Item getItemOrThrow(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_NOT_FOUND));
    }

    private Item getActiveItem(Long itemId) {
        Item item = getItemOrThrow(itemId);
        if (item.isDeleted()) {
            throw new BusinessException(ErrorCode.ITEM_DELETED);
        }
        return item;
    }

    private void assertOwnerOrAdmin(Item item) {
        if (currentUSer.hasRole("ROLE_ADMIN")) return;
        Long me = currentUSer.currentUserId();
        Long sellerId = (item.getSeller() != null) ? item.getSeller().getId() : null;
        if (!Objects.equals(me, sellerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_OPERATION);
        }
    }

    private boolean notDeleted(Item item) {
        return !item.isDeleted();
    }

    private boolean filterByKeyword(Item item, String keyword) {
        if (keyword == null || keyword.isBlank()) return true;
        String k = keyword.toLowerCase();
        return (item.getName() != null && item.getName().toLowerCase().contains(k))
                || (item.getBrand() != null && item.getBrand().toLowerCase().contains(k));
    }

    private boolean filterByStatus(Item item, ItemStatus status) {
        return status == null || item.getStatus() == status;
    }

    private boolean filterByCategory(Item item, Long categoryId) {
        if (categoryId == null) return true;
        return item.getCategory() != null && Objects.equals(item.getCategory().getId(), categoryId);
    }

    private Pageable buildPageable(ItemSearchRequest req) {
        Sort sort = parseSort(req.sort());
        int page = Math.max(req.page(), 0);
        int size = Math.max(req.size(), 1);
        return PageRequest.of(page, size, sort);
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            // BaseEntity(혹은 상위)에서 createdAt 정렬이 가능하다고 가정
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        try {
            String[] parts = sort.split(",");
            String prop = parts[0].trim();
            Sort.Direction dir = (parts.length > 1 ? Sort.Direction.fromString(parts[1].trim()) : Sort.Direction.ASC);

            // 화이트리스트
            Set<String> allowed = Set.of("createdAt", "price", "salesVolume", "name", "brand");
            if (!allowed.contains(prop)) throw new IllegalArgumentException();

            return Sort.by(dir, prop);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_SORT);
        }
    }

    private String normalizeNullable(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private void validateCreate(ItemCreateRequest req) {
        requireNonBlankMax(req.name(), 50, "name");
        requireNonBlankMax(req.brand(), 30, "brand");
        requirePositive(req.price(), "price");
        // description은 null/blank 허용
    }

    private void requireNonBlankMax(String value, int max, String field) {
        if (value == null || value.isBlank() || value.length() > max) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }
    }

    private void requirePositive(java.math.BigDecimal price, String field) {
        if (price == null || price.signum() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAM);
        }
    }

    private ItemResponse toListResponse(Item item) {
        Long categoryId = item.getCategory() != null ? item.getCategory().getId() : null;

        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getBrand(),
                item.getStatus(),
                categoryId,
                item.isDeleted(),
                item.getSalesVolume()
        );
    }

    private ItemDetailResponse toDetailResponse(Item item) {
        Long categoryId = item.getCategory() != null ? item.getCategory().getId() : null;
        Long sellerId = (item.getSeller() != null) ? item.getSeller().getId() : null;

        return new ItemDetailResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getBrand(),
                item.getStatus(),
                sellerId,
                categoryId,
                item.isDeleted(),
                item.getSalesVolume()
        );
    }
}