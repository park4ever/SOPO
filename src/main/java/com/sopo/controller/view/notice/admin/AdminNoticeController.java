package com.sopo.controller.view.notice.admin;

import com.sopo.dto.notice.request.AdminNoticeCreateRequest;
import com.sopo.dto.notice.request.AdminNoticeUpdateRequest;
import com.sopo.dto.notice.response.AdminNoticeDetailResponse;
import com.sopo.dto.notice.response.AdminNoticeSummaryResponse;
import com.sopo.security.aop.AdminOnly;
import com.sopo.service.notice.AdminNoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/notices")
@AdminOnly("ROLE_ADMIN")
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;

    @GetMapping
    public String list(Model model) {
        List<AdminNoticeSummaryResponse> notices = adminNoticeService.getAll();
        model.addAttribute("notices", notices);
        return "admin/notice/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long noticeId, Model model) {
        AdminNoticeDetailResponse notice = adminNoticeService.getById(noticeId);
        model.addAttribute("notice", notice);
        return "admin/notice/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new AdminNoticeCreateRequest("", "", false));
        return "admin/notice/new";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("form") AdminNoticeCreateRequest form) {
        Long id = adminNoticeService.create(form);
        return "redirect:/admin/notices/" + id;
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long noticeId, Model model) {
        AdminNoticeDetailResponse notice = adminNoticeService.getById(noticeId);
        AdminNoticeUpdateRequest form = new AdminNoticeUpdateRequest(
                notice.version(),
                notice.title(),
                notice.content(),
                notice.pinned()
        );

        model.addAttribute("notice", notice);
        model.addAttribute("form", form);
        return "admin/notice/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") Long noticeId,
                         @Valid @ModelAttribute("form") AdminNoticeUpdateRequest form) {
        adminNoticeService.update(noticeId, form);
        return "redirect:/admin/notices/" + noticeId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long noticeId) {
        adminNoticeService.delete(noticeId);
        return "redirect:/admin/notices";
    }
}