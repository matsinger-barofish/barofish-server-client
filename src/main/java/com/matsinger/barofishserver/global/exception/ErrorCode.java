package com.matsinger.barofishserver.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    DEFAULT_ERROR("9999", "예기치 못한 오류가 발생했습니다." + "\n" + "불편을 드려 죄송합니다."),

    TOKEN_REQUIRED("101", "로그아웃 되었습니다." + "\n" + "다시 로그인해 주세요."),
    TOKEN_EXPIRED("102", "로그아웃 되었습니다." + "\n" + "다시 로그인해 주세요."),
    TOKEN_INVALID("103", "로그아웃 되었습니다." + "\n" + "다시 로그인해 주세요."),
    NOT_ALLOWED("104", "접근 권한이 없습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    ErrorCode(String message) {
        this.code = "1000";
        this.message = message;
    }
}
