package com.sopo.exception;

import com.sopo.dto.common.response.ErrorResponse;
import com.sopo.dto.common.response.ErrorResponse.Violation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.sopo.controller.api")
public class ApiExceptionHandler {

    // 1) 비즈니스 예외 (커스텀)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest req) {
        var code = ex.getErrorCode();
        var body = new ErrorResponse(
                LocalDateTime.now(),
                req.getRequestURI(),
                code.status().value(),
                code.name(),
                ex.getMessage(),
                List.of()
        );
        return ResponseEntity.status(code.status()).body(body);
    }

    // 2) @Valid @RequestBody 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInvalid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new Violation(
                        fe.getField(),
                        fe.getRejectedValue() == null ? "" : String.valueOf(fe.getRejectedValue()),
                        fe.getDefaultMessage()
                ))
                .toList();

        var body = new ErrorResponse(
                LocalDateTime.now(),
                req.getRequestURI(),
                BAD_REQUEST.value(),
                "INVALID_ARGUMENT",
                "요청 값이 올바르지 않습니다.",
                violations
        );
        return ResponseEntity.badRequest().body(body);
    }

    // 3) @ModelAttribute 등 바인딩 실패
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBind(BindException ex, HttpServletRequest req) {
        List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new Violation(
                        fe.getField(),
                        fe.getRejectedValue() == null ? "" : String.valueOf(fe.getRejectedValue()),
                        fe.getDefaultMessage()
                ))
                .toList();

        var body = new ErrorResponse(
                LocalDateTime.now(),
                req.getRequestURI(),
                BAD_REQUEST.value(),
                "BIND_ERROR",
                "요청 값이 올바르지 않습니다.",
                violations
        );
        return ResponseEntity.badRequest().body(body);
    }

    // 4) @Validated + 경로/쿼리 검증 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        List<Violation> violations = ex.getConstraintViolations().stream()
                .map(v -> new Violation(v.getPropertyPath().toString(), "", v.getMessage()))
                .toList();

        var body = new ErrorResponse(
                LocalDateTime.now(),
                req.getRequestURI(),
                BAD_REQUEST.value(),
                "CONSTRAINT_VIOLATION",
                "요청 값이 올바르지 않습니다.",
                violations
        );
        return ResponseEntity.badRequest().body(body);
    }

    // 5) 요청 형식/메서드/미디어 타입 등
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest req) {
        var body = ErrorResponse.empty(req.getRequestURI(), BAD_REQUEST.value(), "BAD_REQUEST", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        var body = ErrorResponse.empty(req.getRequestURI(), METHOD_NOT_ALLOWED.value(), "METHOD_NOT_ALLOWED", ex.getMessage());
        return ResponseEntity.status(METHOD_NOT_ALLOWED).body(body);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMedia(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        var body = ErrorResponse.empty(req.getRequestURI(), UNSUPPORTED_MEDIA_TYPE.value(), "UNSUPPORTED_MEDIA_TYPE", ex.getMessage());
        return ResponseEntity.status(UNSUPPORTED_MEDIA_TYPE).body(body);
    }

    // 6) 보안
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        var body = ErrorResponse.empty(req.getRequestURI(), FORBIDDEN.value(), "ACCESS_DENIED", "접근 권한이 없습니다.");
        return ResponseEntity.status(FORBIDDEN).body(body);
    }

    // 6.5) 동시성(낙관적 락 충돌)
    @ExceptionHandler(org.springframework.dao.OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockSpring(
            org.springframework.dao.OptimisticLockingFailureException ex,
            HttpServletRequest req
    ) {
        var code = ErrorCode.OPTIMISTIC_LOCK_CONFLICT;
        var body = ErrorResponse.empty(req.getRequestURI(),
                code.status().value(), code.name(), code.defaultMessage());
        log.warn("Optimistic lock conflict: {}", ex.getMessage());
        return ResponseEntity.status(code.status()).body(body);
    }

    @ExceptionHandler(jakarta.persistence.OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockJpa(
            jakarta.persistence.OptimisticLockException ex,
            HttpServletRequest req
    ) {
        var code = ErrorCode.OPTIMISTIC_LOCK_CONFLICT;
        var body = ErrorResponse.empty(req.getRequestURI(),
                code.status().value(), code.name(), code.defaultMessage());
        return ResponseEntity.status(code.status()).body(body);
    }


    // 7) DB 무결성
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        var body = ErrorResponse.empty(req.getRequestURI(), CONFLICT.value(), "DATA_INTEGRITY_VIOLATION", "데이터 무결성 제약 조건을 위반했습니다.");
        return ResponseEntity.status(CONFLICT).body(body);
    }

    // 8) 최종 방어선
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleEtc(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception", ex);
        var body = ErrorResponse.empty(req.getRequestURI(), INTERNAL_SERVER_ERROR.value(), "INTERNAL_ERROR", "서버 오류가 발생했습니다.");
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(body);
    }
}