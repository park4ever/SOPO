package com.sopo.service.item;

import com.sopo.domain.item.ItemCategory;
import com.sopo.dto.item.request.ItemCategoryCreateRequest;
import com.sopo.dto.item.request.ItemCategoryMoveRequest;
import com.sopo.dto.item.request.ItemCategoryRenameRequest;
import com.sopo.dto.item.response.ItemCategoryDetailResponse;
import com.sopo.dto.item.response.ItemCategoryPathNode;
import com.sopo.dto.item.response.ItemCategoryTreeNode;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.repository.item.ItemCategoryRepository;
import com.sopo.security.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemCategoryServiceImpl implements ItemCategoryService {

    private final ItemCategoryRepository categoryRepository;
    private final CurrentUserProvider currentUser;

    @Override
    public Long create(ItemCategoryCreateRequest request) {
        assertAdmin();

        ItemCategory parent = null;
        if (request.parentId() != null) {
            parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
            if (parent.isDeleted()) {
                throw new BusinessException(ErrorCode.CATEGORY_DELETED_PARENT);
            }
        }

        //동일 부모 하위 이름 중복 금지(삭제된 항목 제외)
        if (categoryRepository.existsSiblingName(request.parentId(), request.name())) {
            throw new BusinessException(ErrorCode.CATEGORY_DUPLICATE_NAME);
        }

        ItemCategory created = ItemCategory.create(request.name(), parent);

        return categoryRepository.save(created).getId();
    }

    @Override
    public void rename(Long categoryId, ItemCategoryRenameRequest request) {
        assertAdmin();

        ItemCategory category = getActive(categoryId);
        Long parentId = (category.getParent() != null) ? category.getParent().getId() : null;

        //동일 이름으로 변경 시 빠르게 리턴
        if (category.getName().equals(request.newName())) return;

        //자기 자신 제외 형제 중복 검사
        if (categoryRepository.existsSiblingNameExcludingSelf(category.getId(), parentId, request.newName())) {
            throw new BusinessException(ErrorCode.CATEGORY_DUPLICATE_NAME);
        }

        category.rename(request.newName());
    }

    @Override
    public void move(Long categoryId, ItemCategoryMoveRequest request) {
        assertAdmin();

        ItemCategory target = getActive(categoryId);
        ItemCategory newParent = null;

        if (request.newParentId() != null) {
            newParent = categoryRepository.findById(request.newParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
            if (newParent.isDeleted()) {
                throw new BusinessException(ErrorCode.CATEGORY_MOVE_TO_DELETED_PARENT);
            }
        }

        Long afterParentId = (newParent != null) ? newParent.getId() : null;

        //이동 후, 부모 기준으로 이름 중복 방지(자기 자신 제외)
        if (categoryRepository.existsSiblingNameExcludingSelf(target.getId(), afterParentId, target.getName())) {
            throw new BusinessException(ErrorCode.CATEGORY_DUPLICATE_NAME);
        }

        //순환 방지 + depth 재계산은 도메인에 위임
        try {
            target.moveTo(newParent);
        } catch (IllegalArgumentException e) {
            //도메인에서 순환 감지 시, 던지는 예외를 Conflict로 매핑
            throw new BusinessException(ErrorCode.CATEGORY_CYCLE);
        }
    }

    @Override
    public void softDelete(Long categoryId) {
        assertAdmin();

        ItemCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        category.markAsDeleted();
    }

    @Override
    public void restore(Long categoryId) {
        assertAdmin();

        ItemCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        //부모가 삭제 상태면 복구 금지
        if (category.getParent() != null && category.getParent().isDeleted()) {
            throw new BusinessException(ErrorCode.CATEGORY_DELETED_PARENT);
        }

        category.unsetDeleted();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemCategoryDetailResponse getById(Long categoryId) {
        ItemCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        return toDetail(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemCategoryDetailResponse> listChildren(Long parentId, boolean includeDeleted) {
        List<ItemCategory> children = categoryRepository.findChildren(parentId, includeDeleted);

        return children.stream().map(this::toDetail).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemCategoryTreeNode> getTree(boolean includeDeleted) {
        List<ItemCategory> all = categoryRepository.findAllForTree(includeDeleted);

        //id -> 노드 매핑
        Map<Long, ItemCategoryTreeNode> nodeMap = new LinkedHashMap<>();
        for (ItemCategory c : all) {
            nodeMap.put(c.getId(), new ItemCategoryTreeNode(
                    c.getId(), c.getName(), c.getDepth(), c.isDeleted(), new ArrayList<>()
            ));
        }

        //부모 <-> 자식 연결
        List<ItemCategoryTreeNode> roots = new ArrayList<>();
        for (ItemCategory c : all) {
            ItemCategoryTreeNode node = nodeMap.get(c.getId());
            if (c.getParent() == null) {
                roots.add(node);
            } else {
                ItemCategoryTreeNode parent = nodeMap.get(c.getParent().getId());
                if (parent != null) parent.children().add(node);
            }
        }

        return roots;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemCategoryPathNode> getPath(Long categoryId) {
        ItemCategory c = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        LinkedList<ItemCategoryPathNode> path = new LinkedList<>();
        ItemCategory cur = c;
        while (cur != null) {
            path.addFirst(new ItemCategoryPathNode(cur.getId(), cur.getName()));
            cur = cur.getParent();
        }

        return path;
    }

    private ItemCategory getByIdOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private ItemCategory getActive(Long id) {
        ItemCategory category = getByIdOrThrow(id);
        if (category.isDeleted()) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return category;
    }

    private Long idOf(ItemCategory category) {
        return (category == null) ? null : category.getId();
    }

    private ItemCategoryDetailResponse toDetail(ItemCategory category) {
        Long parentId = (category.getParent() != null) ? category.getParent().getId() : null;
        return new ItemCategoryDetailResponse(
                category.getId(), category.getName(), parentId, category.getDepth(), category.isDeleted(), category.getChildren().size()
        );
    }

    private void assertAdmin() {
        if (!currentUser.hasRole("ROLE_ADMIN")) {
            throw new BusinessException(ErrorCode.FORBIDDEN_OPERATION);
        }
    }
}