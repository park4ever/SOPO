package com.sopo.service.item;

import com.sopo.dto.item.request.*;
import com.sopo.dto.item.response.ItemDetailResponse;
import com.sopo.dto.item.response.ItemResponse;
import org.springframework.data.domain.Page;

public interface ItemService {

    // Item 기본
    Long create(ItemCreateRequest request);
    void update(Long itemId, ItemUpdateRequest request);
    void changeStatus(Long itemId, ItemStatusChangeRequest request);
    void softDelete(Long itemId);
    void restore(Long itemId);
    ItemDetailResponse getById(Long itemId);
    org.springframework.data.domain.Page<ItemResponse> search(ItemSearchRequest request);
    void increaseSalesVolume(Long itemId, int amount);
    void decreaseSalesVolume(Long itemId, int amount);

    // Image
    Long addImage(Long itemId, ItemImageCreateRequest request);
    void removeImage(Long itemId, Long imageId);
    void reorderImages(Long itemId, ItemImageReorderRequest request);
    void setThumbnail(Long itemId, Long imageId); // 단일 썸네일 보장

    // Option
    Long addOption(Long itemId, ItemOptionCreateRequest request);
    void removeOption(Long itemId, Long optionId);
    void decreaseOptionStock(Long itemId, ItemOptionStockChangeRequest request);
    void increaseOptionStock(Long itemId, ItemOptionStockChangeRequest request);
    void markOptionSoldOut(Long itemId, Long optionId);
    void markOptionInStock(Long itemId, Long optionId);
}