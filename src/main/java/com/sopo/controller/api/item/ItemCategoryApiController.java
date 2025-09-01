package com.sopo.controller.api.item;

import com.sopo.dto.item.response.ItemCategoryDetailResponse;
import com.sopo.dto.item.response.ItemCategoryPathNode;
import com.sopo.dto.item.response.ItemCategoryTreeNode;
import com.sopo.service.item.ItemCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class ItemCategoryApiController {

    private final ItemCategoryService categoryService;

    @GetMapping("/{id}")
    public ResponseEntity<ItemCategoryDetailResponse> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ItemCategoryDetailResponse>> listChildren(
            @RequestParam(required = false) Long parentId,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        return ResponseEntity.ok(categoryService.listChildren(parentId, includeDeleted));
    }

    @GetMapping("/tree")
    public ResponseEntity<List<ItemCategoryTreeNode>> getTree(
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        return ResponseEntity.ok(categoryService.getTree(includeDeleted));
    }

    @GetMapping("/{id}/path")
    public ResponseEntity<List<ItemCategoryPathNode>> getPath(@PathVariable("id") Long id) {
        return ResponseEntity.ok(categoryService.getPath(id));
    }
}