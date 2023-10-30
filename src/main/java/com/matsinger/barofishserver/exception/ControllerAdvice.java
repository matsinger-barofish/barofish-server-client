package com.matsinger.barofishserver.exception;

import com.matsinger.barofishserver.user.api.UserController;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<CustomResponse<Object>> test(Exception e) {

        log.error("error = {}", e.toString());
        CustomResponse customResponse = new CustomResponse();

        customResponse.setIsSuccess(false);
        customResponse.setErrorMsg(e.getMessage());

        return ResponseEntity.ok(customResponse);
    }

}
