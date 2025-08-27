package com.sopo.service.item;

public interface ItemCategoryService {

    Long create(String name, Long parentId);          // parentId null 허용
    void rename(Long categoryId, String newName);
    void move(Long categoryId, Long newParentId);     // depth 재계산 + 순환 방지
    void softDelete(Long categoryId);
    void restore(Long categoryId);
    /*ItemCategoryDetailResponse getById(Long id);
    java.util.List<ItemCategoryTreeNode> getTree();   // 트리 응답*/
}