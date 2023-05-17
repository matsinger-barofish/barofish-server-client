package com.matsinger.barofishserver.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Getter
@Setter
public class CustomResponse<T> {
    private Boolean isSuccess = true;
    private String ErrorMsg = null;
    private String code = null;
    private Optional<T> data;

    public ResponseEntity<CustomResponse<T>> throwError(String errorMsg, String code) {
        this.setIsSuccess(false);
        this.setErrorMsg(errorMsg);
        this.setCode(code);
        return ResponseEntity.ok(this);
    }

    public ResponseEntity<CustomResponse<T>> defaultError(Exception e) {
        this.setIsSuccess(false);
        this.setErrorMsg(e.getMessage());
        this.setCode("INTERNAL_SERVER_ERROR");
        this.setData(null);
        return ResponseEntity.ok(this);
    }
}
