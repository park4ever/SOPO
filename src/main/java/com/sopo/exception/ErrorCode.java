package com.sopo.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // -------------------- 400 Bad Request: 요청 형식/값 오류 --------------------
    INVALID_PARAM(HttpStatus.BAD_REQUEST, "올바르지 않은 요청입니다."),
    INVALID_SORT(HttpStatus.BAD_REQUEST, "지원하지 않는 정렬 조건입니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "유효하지 않은 수량입니다."),
    ORDER_EMPTY_LINES(HttpStatus.BAD_REQUEST, "주문 항목이 비어 있습니다."),

    // -------------------- 401 Unauthorized: 미인증/인증 실패 --------------------
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),

    // -------------------- 403 Forbidden: 권한 부족/상태로 인해 금지 --------------------
    FORBIDDEN_OPERATION(HttpStatus.FORBIDDEN, "요청한 작업에 대한 권한이 없습니다."),
    MEMBER_DISABLED(HttpStatus.FORBIDDEN, "비활성화된 회원입니다."),
    ITEM_DELETED(HttpStatus.FORBIDDEN, "삭제된 상품은 사용할 수 없습니다."),
    ITEM_NOT_ON_SALE(HttpStatus.FORBIDDEN, "판매 중이 아닌 상품입니다."),
    ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 주문에 접근할 권한이 없습니다."),
    ORDER_DELETED(HttpStatus.FORBIDDEN, "삭제된 주문입니다."),

    // -------------------- 404 Not Found: 리소스 없음 --------------------
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "주소 정보를 찾을 수 없습니다."),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 정보를 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "이미지 정보를 찾을 수 없습니다."),
    OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "옵션 정보를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리 정보를 찾을 수 없습니다."),
    COLOR_NOT_FOUND(HttpStatus.NOT_FOUND, "색상 정보를 찾을 수 없습니다."),
    SIZE_NOT_FOUND(HttpStatus.NOT_FOUND, "사이즈 정보를 찾을 수 없습니다."),
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니를 찾을 수 없습니다."),
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니 항목을 찾을 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 항목을 찾을 수 없습니다."),

    // -------------------- 409 Conflict: 상태 충돌/중복 --------------------
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    DUPLICATE_ITEM_OPTION(HttpStatus.CONFLICT, "이미 존재하는 색상/사이즈 조합입니다."),
    CATEGORY_DUPLICATE_NAME(HttpStatus.CONFLICT, "동일 부모 내에서 이미 존재하는 이름입니다."),
    CATEGORY_CYCLE(HttpStatus.CONFLICT, "카테고리를 자기 하위로 이동할 수 없습니다."),
    CATEGORY_DELETED_PARENT(HttpStatus.CONFLICT, "삭제된 부모 카테고리에는 추가/이동할 수 없습니다."),
    CATEGORY_MOVE_TO_DELETED_PARENT(HttpStatus.CONFLICT, "삭제된 부모 카테고리로는 이동할 수 없습니다."),
    OPTION_SOLD_OUT(HttpStatus.CONFLICT, "품절된 옵션입니다."),
    QUANTITY_EXCEEDS_STOCK(HttpStatus.CONFLICT, "재고 수량을 초과했습니다."),
    DUPLICATED_CART_ITEM(HttpStatus.CONFLICT, "동일 옵션이 이미 장바구니에 존재합니다."),
    INVALID_ORDER_STATUS_CHANGE(HttpStatus.CONFLICT, "현재 상태에서는 요청한 상태로 변경할 수 없습니다."),
    ORDER_ALREADY_CANCELED(HttpStatus.CONFLICT, "이미 취소된 주문입니다.");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus status() { return status; }
    public String defaultMessage() { return defaultMessage; }
}