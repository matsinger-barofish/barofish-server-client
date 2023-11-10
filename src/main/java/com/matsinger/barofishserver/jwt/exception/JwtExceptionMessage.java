package com.matsinger.barofishserver.jwt.exception;

public class JwtExceptionMessage {

    public static String TOKEN_REQUIRED = "로그인 토큰 정보가 없습니다. 로그인해 주세요.";
    public static String TOKEN_EXPIRED = "토큰 정보가 만료되었습니다. 다시 로그인해 주세요.";
    public static String TOKEN_INVALID = "토큰 값이 잘못되었습니다.";
    public static String NOT_ALLOWED = "접근 권한이 없습니다.";
}
