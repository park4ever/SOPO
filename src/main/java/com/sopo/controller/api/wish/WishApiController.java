package com.sopo.controller.api.wish;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.wish.request.WishCreateRequest;
import com.sopo.dto.wish.response.WishResponse;
import com.sopo.security.session.MemberSession;
import com.sopo.service.wish.WishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class WishApiController {

    private final WishService wishService;

    @PostMapping("/wishes")
    public ResponseEntity<Long> add(@LoginMember MemberSession session,
                                    @Valid @RequestBody WishCreateRequest request) {
        Long id = wishService.add(session.id(), request);
        return ResponseEntity.created(URI.create("/api/wishes/" + id)).body(id);
    }

    @DeleteMapping("/wishes/{wishId}")
    public ResponseEntity<Void> remove(@LoginMember MemberSession session,
                                       @PathVariable("wishId") Long wishId) {
        wishService.remove(session.id(), wishId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{itemId}/wish")
    public ResponseEntity<Void> removeByItem(@LoginMember MemberSession session,
                                             @PathVariable("itemId") Long itemId) {
        wishService.removeByItem(session.id(), itemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/wishes")
    public List<WishResponse> listMine(@LoginMember MemberSession session) {
        return wishService.listMine(session.id());
    }

    @GetMapping("/items/{itemId}/wish")
    public Map<String, Object> exists(@LoginMember MemberSession session,
                                      @PathVariable("itemId") Long itemId) {
        boolean exists = wishService.exists(session.id(), itemId);
        long count = wishService.countForItem(itemId);
        return Map.of("exists", exists, "count", count);
    }

    @PostMapping("/items/{itemId}/wish/toggle")
    public Map<String, Object> toggle(@LoginMember MemberSession session,
                                      @PathVariable("itemId") Long itemId) {
        boolean liked = wishService.toggle(session.id(), itemId);
        long count = wishService.countForItem(itemId);
        return Map.of("liked", liked, "count", count);
    }
}