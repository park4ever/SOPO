package com.sopo.controller.api.item;

import com.sopo.dto.item.request.ItemCategoryCreateRequest;
import com.sopo.dto.item.request.ItemCategoryMoveRequest;
import com.sopo.dto.item.request.ItemCategoryRenameRequest;
import com.sopo.service.item.ItemCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminItemCategoryApiController {

    private final ItemCategoryService categoryService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody ItemCategoryCreateRequest request) {
        Long id = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @PatchMapping("/{id}/rename")
    public ResponseEntity<Void> rename(@PathVariable("id") Long id,
                                       @Valid @RequestBody ItemCategoryRenameRequest request) {
        categoryService.rename(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/move")
    public ResponseEntity<Void> move(@PathVariable("id") Long id,
                                     @Valid @RequestBody ItemCategoryMoveRequest request) {
        categoryService.move(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDelete(@PathVariable("id") Long id,
                                           @RequestParam(name = "cascade", defaultValue = "false") boolean cascade) {
        categoryService.softDelete(id, cascade);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable("id") Long id,
                                        @RequestParam(name = "cascade", defaultValue = "false") boolean cascade) {
        categoryService.restore(id, cascade);
        return ResponseEntity.noContent().build();
    }
}