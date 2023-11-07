package com.matsinger.barofishserver.global.exception;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Order(2)
@Slf4j
@RestControllerAdvice(basePackages = {"com.matsinger.barofishserver"})
@RequiredArgsConstructor
public class GlobalControllerAdvice implements RequestBodyAdvice {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private ThreadLocal<Boolean> afterBodyReadExecuted = ThreadLocal.withInitial(() -> false);

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<CustomResponse<Object>> catchException(
            HttpServletRequest request,
            Exception e) throws IOException {

        printExceptionInfo(request, e);

        CustomResponse customResponse = new CustomResponse();
        customResponse.setIsSuccess(false);
        customResponse.setErrorMsg("예기치 못한 오류가 발생했습니다. 불편을 드려 죄송합니다.");
        return ResponseEntity.ok(customResponse);
    }

    private void printExceptionInfo(HttpServletRequest request, Exception e) {
        if (!afterBodyReadExecuted.get()) {
            logger.warn("### Exception Start ###");
            logger.warn(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()));
        }

        logger.warn("url = {}", request.getRequestURI());
        logger.warn("method = {}", request.getMethod());
        logger.warn("queryString = {}", request.getQueryString());
        request.getHeaderNames().asIterator().forEachRemaining(
                header -> logger.warn("header = {}", request.getHeader(header))
        );
        request.getParameterNames().asIterator().forEachRemaining(
                parameter -> logger.warn("parameter = {}", request.getParameter(parameter))
        );
        e.printStackTrace();

        logger.warn("### Exception End ###");
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

        logger.warn("### Exception Start ###");
        logger.warn("Exception Date = {}", DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").format(LocalDateTime.now()));

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
