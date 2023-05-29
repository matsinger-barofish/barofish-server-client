package com.matsinger.barofishserver.order.exception;

public class OrderErrorMessage {
    public static final String ORDER_SEQUENCE_EXCEPTION = "분당 주문 수가 9999를 초과했습니다.";
    public static final String USER_NOT_FOUND_EXCEPTION = "유저를 찾을 수 없습니다.";
    public static final String PRODUCT_NOT_FOUND_EXCEPTION = "상품을 찾을 수 없습니다.";
    public static final String USER_AUTH_NOT_FOUND_EXCEPTION = "UserAuth를 찾을 수 없습니다.";
    public static final String ORDER_NOT_FOUND_EXCEPTION = "주문 정보를 찾을 수 없습니다.";
    public static final String OPTION_NOT_FOUND_EXCEPTION = "상품 옵션을 찾을 수 없습니다.";
    public static final String STORE_NOT_FOUND_EXCEPTION = "스토어를 찾을 수 없습니다.";
    public static final String ORDER_SAVE_FAIL_EXCEPTION = "주문을 생성하는데 실패했습니다.";
}
