package com.matsinger.barofishserver.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode code;

    public BusinessException(ErrorCode code) {
        super();
        this.code = code;
    }

    public BusinessException(String message, Throwable cause, ErrorCode code) {
        super(message, cause);
        this.code = code;
    }

    public BusinessException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }

    public BusinessException(Throwable cause, ErrorCode code) {
        super(cause);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = null;
    }
}
