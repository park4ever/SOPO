package com.sopo.dto.common.response;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        String path,
        int status,          // HTTP status code (e.g., 400)
        String code,         // 내부 에러 코드 (e.g., EMAIL_DUPLICATED / INVALID_ARGUMENT)
        String message,      // 사용자 메시지
        List<Violation> errors // 필드 단위 검증 실패 목록
) {
    // 편의 생성자: 필드 에러 없는 경우
    public static ErrorResponse empty(String path, int status, String code, String message) {
        return new ErrorResponse(LocalDateTime.now(), path, status, code, message, Collections.emptyList());
    }

    // 검증 에러 1건 표현 (스프링 FieldError와 이름 충돌 피하려고 Violation 사용)
    public record Violation(String field, String value, String reason) { }
}