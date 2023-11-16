package com.matsinger.barofishserver.utils;

import com.matsinger.barofishserver.global.error.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@Getter
@Setter
public class CustomResponse<T> {
    private Boolean isSuccess = true;
    private String ErrorMsg = null;
    private String code = null;
    private Optional<T> data;

    public void setCode(ErrorCode errorCode) {
        this.ErrorMsg = errorCode.getMessage();
        this.code = errorCode.getCode();
    }

    public void setCode(String errorCode) {
        this.code = errorCode;
    }
}
