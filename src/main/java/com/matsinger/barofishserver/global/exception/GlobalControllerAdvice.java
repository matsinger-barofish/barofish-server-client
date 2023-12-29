package com.matsinger.barofishserver.global.exception;

import com.matsinger.barofishserver.global.filter.MultiAccessRequestWrapper;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.LoggingUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@RestControllerAdvice(basePackages = {"com.matsinger.barofishserver"})
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final JwtService jwtService;
    private final LoggingUtils loggingUtils;

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<CustomResponse<Object>> handleRuntimeException(
            HttpServletRequest request,
            RuntimeException e) throws IOException {

        MultiAccessRequestWrapper wrapRequest = (MultiAccessRequestWrapper) request;

        String jwtToken = extractBearerToken(wrapRequest);
        TokenInfo tokenInfo = jwtService.extractIdAndAuthType(jwtToken);
        tokenInfo.getId();

        loggingUtils.error(wrapRequest, e);

        CustomResponse customResponse = new CustomResponse();
        customResponse.setIsSuccess(false);
        customResponse.setErrorMsg("예기치 못한 오류가 발생했습니다." + "\n" + "불편을 드려 죄송합니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(customResponse);
    }

    private String extractBearerToken(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();

        String token = null;
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            String value = request.getHeader(header);

            // 현재 헤더가 찾고자 하는 헤더인지 확인합니다.
            if ("Authorization".equalsIgnoreCase(header) && value.startsWith("Bearer ")) {
                // 토큰 부분을 추출합니다.
                return value.substring("Bearer ".length());
            }
        }
        return null;
    }

    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<CustomResponse<Object>> handleBusinessException(
            HttpServletRequest request,
            RuntimeException e) throws IOException {

        MultiAccessRequestWrapper wrapRequest = (MultiAccessRequestWrapper) request;

        loggingUtils.warn(wrapRequest, e);

        CustomResponse customResponse = new CustomResponse();
        customResponse.setIsSuccess(false);
        customResponse.setErrorMsg(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(customResponse);
    }
}
