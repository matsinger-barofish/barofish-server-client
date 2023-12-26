package com.matsinger.barofishserver.global.exception;

import com.matsinger.barofishserver.global.error.ErrorCode;
import com.matsinger.barofishserver.utils.CustomResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

@Slf4j
@Order(2)
@RestControllerAdvice(basePackages = {"com.matsinger.barofishserver"})
@RequiredArgsConstructor
public class GlobalControllerAdvice implements RequestBodyAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private ThreadLocal<Boolean> afterBodyReadExecuted = ThreadLocal.withInitial(() -> false);

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<CustomResponse<Object>> catchException(
            HttpServletRequest request,
            Exception e) {

        printExceptionInfo(request, e);

        CustomResponse customResponse = new CustomResponse();
        customResponse.setIsSuccess(false);
        customResponse.setCode(ErrorCode.DEFAULT_ERROR);
        return ResponseEntity.ok(customResponse);
    }

    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<CustomResponse<Object>> catchBusinessException(
            HttpServletRequest request,
            Exception e) {

        printExceptionInfo(request, e);

        CustomResponse customResponse = new CustomResponse();
        customResponse.setIsSuccess(false);

        customResponse.setErrorMsg(e.getMessage());
        customResponse.setCode("1000");

        return ResponseEntity.ok(customResponse);
    }

    private void printExceptionInfo(HttpServletRequest request, Exception e) {

        logger.warn(e.getMessage());
        e.printStackTrace();
    }

    @Override
    public boolean supports(
            MethodParameter methodParameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(
            Object body,
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {

        afterBodyReadExecuted.set(true);

        // @RequestBody 필드 정보 출력
        logger.warn("RequestBody = {}", body.toString());

        return body;
    }

    @Override
    public Object handleEmptyBody(
            Object body,
            HttpInputMessage inputMessage,
            MethodParameter parameter,
            Type targetType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return null;
    }
}
