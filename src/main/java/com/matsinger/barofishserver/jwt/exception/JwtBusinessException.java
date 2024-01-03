package com.matsinger.barofishserver.jwt.exception;

import com.matsinger.barofishserver.global.ErrorCode;
import lombok.Getter;

@Getter
public class JwtBusinessException extends RuntimeException {

    private final ErrorCode code;

    public JwtBusinessException(ErrorCode code) {
        super();
        this.code = code;
    }

    public JwtBusinessException(String message, Throwable cause, ErrorCode code) {
        super(message, cause);
        this.code = code;
    }

    public JwtBusinessException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }

    public JwtBusinessException(Throwable cause, ErrorCode code) {
        super(cause);
        this.code = code;
    }
}
