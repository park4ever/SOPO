package com.sopo.controller.view.wish;

import com.sopo.config.auth.LoginMember;
import com.sopo.security.session.MemberSession;
import com.sopo.service.wish.WishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/my")
public class WishController {

    private final WishService wishService;

    @GetMapping("/wishes")
    public String listMine(@LoginMember MemberSession session, Model model) {
        model.addAttribute("wishes", wishService.listMine(session.id()));
        return "wish/list";
    }
}