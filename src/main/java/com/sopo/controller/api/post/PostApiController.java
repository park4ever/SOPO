package com.sopo.controller.api.post;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.post.request.PostCreateRequest;
import com.sopo.dto.post.request.PostSearchRequest;
import com.sopo.dto.post.request.PostUpdateRequest;
import com.sopo.dto.post.response.PostDetailResponse;
import com.sopo.dto.post.response.PostSummaryResponse;
import com.sopo.security.session.MemberSession;
import com.sopo.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostApiController {

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<Long> create(@LoginMember MemberSession session,
                                       @Valid @RequestBody PostCreateRequest request) {
        Long id = postService.create(session.id(), request);
        return ResponseEntity.created(URI.create("/api/posts/" + id)).body(id);
    }

    @PatchMapping("/posts/{id}")
    public ResponseEntity<Void> update(@LoginMember MemberSession session,
                                       @PathVariable("id") Long postId,
                                       @Valid @RequestBody PostUpdateRequest request) {
        postService.update(session.id(), postId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> delete(@LoginMember MemberSession session,
                                       @PathVariable("id") Long postId) {
        postService.delete(session.id(), postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/posts/{id}")
    public PostDetailResponse get(@PathVariable("id") Long postId) {
        return postService.get(postId);
    }

    @GetMapping("/posts")
    public Page<PostSummaryResponse> search(PostSearchRequest request, Pageable pageable) {
        return postService.search(request.toCond(), pageable);
    }

    @GetMapping("/posts/mine")
    public Page<PostSummaryResponse> myPosts(@LoginMember MemberSession session,
                                             Pageable pageable) {
        return postService.getByMember(session.id(), pageable);
    }
}