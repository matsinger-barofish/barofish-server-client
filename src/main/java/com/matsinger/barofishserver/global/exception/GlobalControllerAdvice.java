package com.matsinger.barofishserver.global.exception;

import com.matsinger.barofishserver.global.filter.MultiAccessRequestWrapper;
import com.matsinger.barofishserver.jwt.exception.JwtBusinessException;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.LoggingUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
@RestControllerAdvice(basePackages = {"com.matsinger.barofishserver"})
@RequiredArgsConstructor
public class GlobalControllerAdvice implements HandlerInterceptor {

    private final LoggingUtils loggingUtils;

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<CustomResponse<Object>> handleRuntimeException(
            HttpServletRequest request,
            RuntimeException e) throws IOException {

        loggingUtils.doLog(request, e, "error");

        CustomResponse customResponse = new CustomResponse();
        customResponse.setIsSuccess(false);
        customResponse.setErrorMsg("예기치 못한 오류가 발생했습니다." + "\n" + "불편을 드려 죄송합니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(customResponse);
    }

    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<CustomResponse<Object>> handleBusinessException(
            HttpServletRequest request,
            RuntimeException e) throws IOException {

        loggingUtils.doLog(request, e, "warn");

        CustomResponse customResponse = new CustomResponse();
        customResponse.setIsSuccess(false);
        customResponse.setErrorMsg(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(customResponse);
    }

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
        return ResponseEntity.status(HttpStatus.OK).body(customResponse);
    }
}
