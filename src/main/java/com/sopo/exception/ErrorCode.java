package com.sopo.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    MEMBER_DISABLED(HttpStatus.FORBIDDEN, "비활성화된 회원입니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인가되지 않은 접근입니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "주소 정보를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus status() { return status; }
    public String defaultMessage() { return defaultMessage; }
}
