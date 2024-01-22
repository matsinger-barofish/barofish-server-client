package com.matsinger.barofishserver.jwt.exception;

import com.matsinger.barofishserver.global.filter.MultiAccessRequestWrapper;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.LoggingUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice(basePackages = {"com.matsinger.barofishserver"})
@RequiredArgsConstructor
public class JwtControllerAdvice {

    private final LoggingUtils loggingUtils;

    @ExceptionHandler(value = {JwtBusinessException.class})
    public ResponseEntity<CustomResponse<Object>> getJwtExpiredMessage(
            HttpServletRequest request,
            JwtBusinessException e) throws IOException {

        MultiAccessRequestWrapper wrapRequest = (MultiAccessRequestWrapper) request;
        loggingUtils.doLog(wrapRequest, e, "warn");

        CustomResponse customResponse = new CustomResponse();
        customResponse.setIsSuccess(false);
        customResponse.setCode(e.getCode());
        customResponse.setErrorMsg(e.getMessage());
        return ResponseEntity.ok(customResponse);
    }
}
