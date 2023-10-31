package com.matsinger.barofishserver.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matsinger.barofishserver.utils.CustomResponse;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.Iterator;

@Slf4j
@RestControllerAdvice(basePackages = {"com.matsinger.barofishserver.global.exception"})
@RequiredArgsConstructor
public class GlobalControllerAdvice implements RequestBodyAdvice {

    private final ObjectMapper objectMapper;
    private ThreadLocal<Boolean> afterBodyReadExecuted = ThreadLocal.withInitial(() -> false);

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<CustomResponse<Object>> test(HttpServletRequest request, Exception e) throws IOException {

        printExceptionInfo(request, e);

        CustomResponse customResponse = new CustomResponse();
        customResponse.setIsSuccess(false);
        customResponse.setErrorMsg("예기치 못한 오류가 발생했습니다. 불편을 드려 죄송합니다.");
        return ResponseEntity.ok(customResponse);
    }

    private void printExceptionInfo(HttpServletRequest request, Exception e) {
        if (!afterBodyReadExecuted.get()) {
            log.error("### Exception Start ###");
            log.error(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()));
        }

        log.error("url = {}", request.getRequestURI());
        log.error("method = {}", request.getMethod());
        log.error("queryString = {}", request.getQueryString());
        request.getHeaderNames().asIterator().forEachRemaining(
                header -> log.error("header = {}", request.getHeader(header))
        );
        request.getParameterNames().asIterator().forEachRemaining(
                parameter -> log.error("parameter = {}", request.getParameter(parameter))
        );
        e.printStackTrace();

        log.error("### Exception End ###");
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

        log.error("### Exception Start ###");
        log.error("Exception Date = {}", DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()));

        // @RequestBody 필드 정보 출력
        log.error("RequestBody = {}", body.toString());

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
