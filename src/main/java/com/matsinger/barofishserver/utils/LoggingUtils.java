package com.matsinger.barofishserver.utils;

import com.matsinger.barofishserver.global.filter.MultiAccessRequestWrapper;
import com.matsinger.barofishserver.global.filter.PrettyConverter;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    public void error(MultiAccessRequestWrapper request, Exception exception) throws IOException {

        String jwtToken = extractBearerToken(request);
        TokenInfo tokenInfo = jwtService.extractIdAndAuthType(jwtToken);
        Integer id = tokenInfo.getId();

        String url = request.getMethod() + "\t" + request.getRequestURI();
        String queryString = request.getQueryString();

        StackTraceElement[] stackTraceElement = exception != null ? exception.getStackTrace() : null;
        String exceptionInfo = getExceptionInfo(
                exception.getMessage(),
                jwtToken,
                id,
                url,
                queryString,
                converter.convert(request.getContents()),
                stackTraceElement);

        logger.error(exceptionInfo);
    }

    public void warn(MultiAccessRequestWrapper request, Exception exception) throws IOException {

        String jwtToken = extractBearerToken(request);
        TokenInfo tokenInfo = jwtService.extractIdAndAuthType(jwtToken);
        Integer id = tokenInfo.getId();

        String url = request.getMethod() + "\t" + request.getRequestURI();
        String queryString = request.getQueryString();

        StackTraceElement[] stackTraceElement = exception != null ? exception.getStackTrace() : null;
        String exceptionInfo = getExceptionInfo(
                exception.getMessage(),
                jwtToken,
                id,
                url,
                queryString,
                converter.convert(request.getContents()),
                stackTraceElement);

        logger.warn(exceptionInfo);
    }

    public void info(MultiAccessRequestWrapper request, Exception exception) throws IOException {

        String jwtToken = extractBearerToken(request);
        TokenInfo tokenInfo = jwtService.extractIdAndAuthType(jwtToken);
        Integer id = tokenInfo.getId();

        String url = request.getMethod() + "\t" + request.getRequestURI();
        String queryString = request.getQueryString();

        StackTraceElement[] stackTraceElement = exception != null ? exception.getStackTrace() : null;
        String exceptionInfo = getExceptionInfo(
                exception.getMessage(),
                jwtToken,
                id,
                url,
                queryString,
                converter.convert(request.getContents()),
                stackTraceElement);

        logger.info(exceptionInfo);
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
