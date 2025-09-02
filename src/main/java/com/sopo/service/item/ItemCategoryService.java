package com.sopo.service.item;

import com.sopo.dto.item.request.ItemCategoryCreateRequest;
import com.sopo.dto.item.request.ItemCategoryMoveRequest;
import com.sopo.dto.item.request.ItemCategoryRenameRequest;
import com.sopo.dto.item.response.ItemCategoryDetailResponse;
import com.sopo.dto.item.response.ItemCategoryPathNode;
import com.sopo.dto.item.response.ItemCategoryTreeNode;

import java.util.List;

public interface ItemCategoryService {

    Long create(ItemCategoryCreateRequest request);

    void rename(Long categoryId, ItemCategoryRenameRequest request);

    void move(Long categoryId, ItemCategoryMoveRequest request);

    void softDelete(Long categoryId, boolean cascade);

    void restore(Long categoryId, boolean cascade);

    ItemCategoryDetailResponse getById(Long categoryId);

    //parentId가 null이면 루트 목록
    List<ItemCategoryDetailResponse> listChildren(Long parentId, boolean includeDeleted);

    //전체 트리(관리/프론트 공용, includeDeleted는 관리 화면에서만 true)
    List<ItemCategoryTreeNode> getTree(boolean includeDeleted);

    //브레드크럼 등 경로 표시
    List<ItemCategoryPathNode> getPath(Long categoryId);
}