package com.matsinger.barofishserver.utils;

import com.matsinger.barofishserver.global.filter.MultiAccessRequestWrapper;
import com.matsinger.barofishserver.global.filter.PrettyConverter;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingUtils {

    private final JwtService jwtService;

    private final Logger logger = LoggerFactory.getLogger(LoggingUtils.class);
    private final PrettyConverter converter;


    public void doLog(HttpServletRequest request, Exception exception, String errorType) throws IOException {

        String jwtToken = extractBearerToken(request);
        Integer id = getIdFromJwtToken(jwtToken);
        String url = request.getMethod() + "\t" + request.getRequestURI();
        String queryString = request.getQueryString();
        StackTraceElement[] stackTraceElement = exception != null ? exception.getStackTrace() : null;

        // multipart/form-data는 타입 캐스팅을 할 수 없고 바디를 추출할 수 없어 경우의 수를 나눠준다.
        boolean isMultipart = false;
        try {
            isMultipart = request.getContentType().startsWith("multipart/form-data");
        } catch (Exception e) {
            isMultipart = false;
        }

        String requestBody = parseRequest(request, isMultipart);

        String exceptionInfo = getExceptionInfo(
                exception.getMessage(),
                jwtToken,
                id,
                url,
                queryString,
                requestBody,
                stackTraceElement);

        if (errorType.equals("error")) {
            logger.error(exceptionInfo);
        }
        if (errorType.equals("warn")) {
            logger.warn(exceptionInfo);
        }
        if (errorType.equals("info")) {
            logger.info(exceptionInfo);
        }
    }

    private String parseRequest(HttpServletRequest request, boolean isMultipart) throws IOException {
        if (!isMultipart) {
            MultiAccessRequestWrapper wrapRequest = (MultiAccessRequestWrapper) request;
            return converter.convert(wrapRequest.getContents());
        }
        return null;
    }

    @Nullable
    private Integer getIdFromJwtToken(String jwtToken) {
        if (jwtToken == null) {
            return null;
        }
        try {
            TokenInfo tokenInfo = jwtService.extractIdAndAuthType(jwtToken);
            return tokenInfo.getId();
        } catch (Exception e) {
            return null;
        }
    }

    private static String getExceptionInfo(String message,
                                           String jwtToken,
                                           Integer id,
                                           String url,
                                           String queryString,
                                           String requestBody,
                                           StackTraceElement[] stackTraceElement) {


        String exceptionInfo = "";
        exceptionInfo += "\n" + "에러 로그 시작" + "\n";

        if (StringUtils.hasText(message)) {
            exceptionInfo += message + "\n";
        }
        if (StringUtils.hasText(jwtToken)) {
            exceptionInfo += "jwtToken = " + jwtToken + "\n";
        }
        if (StringUtils.hasText(url)) {
            exceptionInfo += "URI = " + url + "\n";
        }
        if (StringUtils.hasText(queryString)) {
            exceptionInfo += "QueryString = " + queryString + "\n";
        }
        if (StringUtils.hasText(String.valueOf(id))) {
            exceptionInfo += "id = " + id + "\n";
        }
        if (StringUtils.hasText(requestBody)) {
            exceptionInfo += "RequestBody = " + requestBody + "\n";
        }
//        String stackTrace = stackTraceElement[0].toString();
//        if (StringUtils.hasText(stackTrace)) {
//            exceptionInfo += stackTrace + "\n";
//        }

        if (stackTraceElement != null || stackTraceElement.length != 0) {
            for (StackTraceElement traceElement : stackTraceElement) {
                exceptionInfo += "\n" + traceElement;
            }
        }
        exceptionInfo += "\n" + "에러 로그 끝" + "\n";
        return exceptionInfo;
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
}
