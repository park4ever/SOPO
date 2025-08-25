package com.sopo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@ControllerAdvice(basePackages = "com.sopo.controller.view")
public class ViewExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public String handleBusiness(BusinessException ex, Model model) {
        // 메시지만 뷰로 전달 (공통 에러 페이지 또는 현재 화면 재표시 시 활용)
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/common"; // templates/error/common.html 준비 권장
    }

    @ExceptionHandler(Exception.class)
    public String handleEtc(Exception ex, Model model) {
        log.error("Unhandled view exception", ex);
        model.addAttribute("errorMessage", "서버 오류가 발생했습니다.");
        return "error/common";
    }
}