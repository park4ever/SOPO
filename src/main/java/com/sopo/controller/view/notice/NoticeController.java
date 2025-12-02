package com.sopo.controller.view.notice;

import com.sopo.dto.notice.response.AdminNoticeDetailResponse;
import com.sopo.dto.notice.response.AdminNoticeSummaryResponse;
import com.sopo.service.notice.AdminNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final AdminNoticeService adminNoticeService;

    @GetMapping
    public String list(Model model) {
        List<AdminNoticeSummaryResponse> notices = adminNoticeService.getAll();
        model.addAttribute("notices", notices);
        return "notice/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long noticeId, Model model) {
        AdminNoticeDetailResponse notice = adminNoticeService.getById(noticeId);
        model.addAttribute("notice", notice);
        return "notice/detail";
    }
}