package com.matsinger.barofishserver.global.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class ServletWrappingFilter extends OncePerRequestFilter {

    /**
     *  request, response는 한번 읽으면 다시 읽을 수 없다.
     *  ContentCachingResponseWrapper.copyBodyToResponse() 메서드를 통해 repose를 캐싱할 수 있지만
     *  request는 캐싱할 수 있는 메서드가 없어 커스터마이징해야 한다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        MultiAccessRequestWrapper wrapRequest = new MultiAccessRequestWrapper(request); // 커스텀 Wrapper
        ContentCachingResponseWrapper wrapResponse = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(wrapRequest, wrapResponse);
        wrapResponse.copyBodyToResponse(); // 이 부분이 핵심이다. 이를 통해 response 를 다시 읽을 수 있다.
    }
}
