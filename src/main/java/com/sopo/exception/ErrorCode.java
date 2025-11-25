package com.sopo.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // -------------------- 400 Bad Request: 요청 형식/값 오류 --------------------
    INVALID_PARAM(HttpStatus.BAD_REQUEST, "올바르지 않은 요청입니다."),
    INVALID_SORT(HttpStatus.BAD_REQUEST, "지원하지 않는 정렬 조건입니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "유효하지 않은 수량입니다."),
    ORDER_EMPTY_LINES(HttpStatus.BAD_REQUEST, "주문 항목이 비어 있습니다."),
    COUPON_INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "유효하지 않은 금액(0 이하)입니다."),
    COUPON_INVALID_PERCENTAGE(HttpStatus.BAD_REQUEST, "유효하지 않은 정율(1~100)입니다."),
    COUPON_INVALID_PERIOD(HttpStatus.BAD_REQUEST, "쿠폰 사용 가능 기간이 올바르지 않습니다."),
    COUPON_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "쿠폰 타입과 값 조합이 올바르지 않습니다."),
    COUPON_MAX_DISCOUNT_REQUIRED(HttpStatus.BAD_REQUEST, "정율 쿠폰의 최대 할인액이 필요합니다."),
    DELIVERY_INVALID_RECEIVER_NAME(HttpStatus.BAD_REQUEST, "수령인 이름이 유효하지 않습니다."),
    DELIVERY_INVALID_RECEIVER_PHONE(HttpStatus.BAD_REQUEST, "수령인 연락처가 유효하지 않습니다."),
    DELIVERY_ADDRESS_REQUIRED(HttpStatus.BAD_REQUEST, "배송 주소는 필수입니다."),
    PAYMENT_INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액이 유효하지 않습니다."),
    REVIEW_INVALID_RATING(HttpStatus.BAD_REQUEST, "유효하지 않은 평점입니다."),

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
    COUPON_NOT_ACTIVE(HttpStatus.FORBIDDEN, "현재 사용 가능한 쿠폰이 아닙니다."),
    COUPON_MIN_ORDER_PRICE_NOT_MET(HttpStatus.FORBIDDEN, "최소 주문 금액 조건을 만족하지 않습니다."),
    COUPON_SOFT_DELETED(HttpStatus.FORBIDDEN, "삭제 처리된 쿠폰입니다."),
    MEMBER_COUPON_NOT_USABLE(HttpStatus.FORBIDDEN, "현재 사용 가능한 상태의 보유 쿠폰이 아닙니다."),
    MEMBER_COUPON_NOT_CANCELABLE(HttpStatus.FORBIDDEN, "해당 보유 쿠폰은 사용 취소할 수 없습니다."),
    ADDRESS_NOT_OWNED_BY_MEMBER(HttpStatus.FORBIDDEN, "해당 주소는 회원 소유가 아닙니다."),
    PAYMENT_FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 결제에 접근할 권한이 없습니다."),
    WISH_FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 찜 정보에 접근할 권한이 없습니다."),
    REVIEW_FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 리뷰에 접근할 권한이 없습니다."),

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
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰 정보를 찾을 수 없습니다."),
    MEMBER_COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "보유 쿠폰 정보를 찾을 수 없습니다."),
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "배송 정보를 찾을 수 없습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."),
    WISH_NOT_FOUND(HttpStatus.NOT_FOUND, "찜 정보를 찾을 수 없습니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰 정보를 찾을 수 없습니다."),
    REVIEW_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰 이미지를 찾을 수 없습니다."),

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
    ORDER_ALREADY_CANCELED(HttpStatus.CONFLICT, "이미 취소된 주문입니다."),
    ORDER_LINE_IMMUTABLE_AFTER_ORDERED(HttpStatus.CONFLICT, "주문 확정 이후에는 주문 항목을 변경할 수 없습니다."),
    OPTIMISTIC_LOCK_CONFLICT(HttpStatus.CONFLICT, "요청 처리 중 충돌이 발생했습니다. 다시 시도해주세요."),
    DELIVERY_ALREADY_EXISTS(HttpStatus.CONFLICT, "이 주문에는 이미 배송 정보가 존재합니다."),
    DELIVERY_UPDATE_NOT_ALLOWED(HttpStatus.CONFLICT, "현재 상태에서는 배송 정보를 변경할 수 없습니다."),
    PAYMENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이 주문에는 이미 결제 정보가 존재합니다."),
    PAYMENT_KEY_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 결제 키입니다."),
    PAYMENT_INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "요청한 결제 상태로 변경할 수 없습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.CONFLICT, "주문 금액과 결제 금액이 일치하지 않습니다."),
    PAYMENT_NOT_ALLOWED_FOR_ORDER_STATUS(HttpStatus.CONFLICT, "현재 주문 상태에서는 결제를 처리할 수 없습니다."),
    WISH_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 찜한 상품입니다."),
    WISH_ITEM_NOT_AVAILABLE(HttpStatus.CONFLICT, "현재 찜할 수 없는 상품입니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 주문 항목에 대한 리뷰가 이미 존재합니다."),
    REVIEW_NOT_ALLOWED_FOR_ORDER_STATUS(HttpStatus.CONFLICT, "현재 주문 상태에서는 리뷰를 작성할 수 없습니다.");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus status() { return status; }
    public String defaultMessage() { return defaultMessage; }
}