package com.sopo.controller.view.post;

import com.sopo.config.auth.LoginMember;
import com.sopo.dto.post.request.PostCreateRequest;
import com.sopo.dto.post.request.PostSearchRequest;
import com.sopo.dto.post.request.PostUpdateRequest;
import com.sopo.dto.post.response.PostDetailResponse;
import com.sopo.dto.post.response.PostSummaryResponse;
import com.sopo.exception.BusinessException;
import com.sopo.exception.ErrorCode;
import com.sopo.security.session.MemberSession;
import com.sopo.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @GetMapping
    public String list(PostSearchRequest request, Pageable pageable, Model model) {
        Page<PostSummaryResponse> posts = postService.search(request.toCond(), pageable);

        model.addAttribute("posts", posts);
        model.addAttribute("search", request);  //폼 값 유지용
        return "post/list";
    }

    @GetMapping("/{id}")
    public String detail(@LoginMember MemberSession session,
                         @PathVariable("id") Long postId, Model model) {
        PostDetailResponse post = postService.get(postId);

        Long loginMemberId = (session != null) ? session.id() : null;
        boolean editable = loginMemberId != null && Objects.equals(post.authorId(), loginMemberId);

        model.addAttribute("post", post);
        model.addAttribute("editable", editable);
        return "post/detail";
    }

    @GetMapping("/new")
    public String createForm(@LoginMember MemberSession session, Model model) {
        if (session == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new PostCreateRequest("", ""));
        }
        return "post/new";
    }

    @PostMapping("/new")
    public String create(@LoginMember MemberSession session,
                         @Valid @ModelAttribute("form") PostCreateRequest form,
                         BindingResult bindingResult,
                         RedirectAttributes ra) {
        if (session == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (bindingResult.hasErrors()) {
            return "post/new";
        }

        Long id = postService.create(session.id(), form);
        ra.addFlashAttribute("createdId", id);
        return "redirect:/posts/" + id;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@LoginMember MemberSession session,
                           @PathVariable("id") Long postId, Model model) {
        if (session == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        PostDetailResponse post = postService.get(postId);

        if (!Objects.equals(post.authorId(), session.id())) {
            throw new BusinessException(ErrorCode.POST_FORBIDDEN_ACCESS);
        }

        PostUpdateRequest form = new PostUpdateRequest(post.title(), post.content(), post.version());

        model.addAttribute("post", post);
        model.addAttribute("form", form);
        return "post/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(@LoginMember MemberSession session,
                       @PathVariable("id") Long postId,
                       @Valid @ModelAttribute("form") PostUpdateRequest form,
                       BindingResult bindingResult, Model model) {
        if (session == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (bindingResult.hasErrors()) {
            PostDetailResponse post = postService.get(postId);
            model.addAttribute("post", post);
            return "post/edit";
        }

        postService.update(session.id(), postId, form);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@LoginMember MemberSession session,
                         @PathVariable("id") Long postId) {
        if (session == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        postService.delete(session.id(), postId);
        return "redirect:/posts";
    }
}