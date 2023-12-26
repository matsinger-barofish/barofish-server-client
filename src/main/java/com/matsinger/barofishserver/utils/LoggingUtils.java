package com.matsinger.barofishserver.utils;

import com.matsinger.barofishserver.global.filter.MultiAccessRequestWrapper;
import com.matsinger.barofishserver.global.filter.PrettyConverter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Component
public class LoggingUtils {

    private static final Logger logger = LoggerFactory.getLogger(LoggingUtils.class);
    private static PrettyConverter converter;

    public LoggingUtils(PrettyConverter converter) {
        this.converter = converter;
    }

    public static void error(MultiAccessRequestWrapper request, Exception exception) throws IOException {

        String url = request.getMethod() + "\t" + request.getRequestURI();
        String queryString = request.getQueryString();

        StackTraceElement[] stackTraceElement = exception != null ? exception.getStackTrace() : null;
        String exceptionInfo = getExceptionInfo(
                exception.getMessage(),
                url,
                queryString,
                converter.convert(request.getContents()),
                stackTraceElement);

        logger.warn(exceptionInfo);
    }

    private static String getExceptionInfo(String message, String url, String queryString, String requestBody, StackTraceElement[] stackTraceElement) {
        String exceptionInfo = "";
        exceptionInfo += "\n" + "에러 로그 시작" + "\n";

        if (StringUtils.hasText(message)) {
            exceptionInfo += message + "\n";
        }
        if (StringUtils.hasText(url)) {
            exceptionInfo += "URI = " + url + "\n";
        }
        if (StringUtils.hasText(queryString)) {
            exceptionInfo += "QueryString = " + queryString + "\n";
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
}
