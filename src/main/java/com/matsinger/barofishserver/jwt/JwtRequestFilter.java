package com.matsinger.barofishserver.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // 실제 JWT 검증을 실행하는 Provider
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 인증에서 제외할 url
    private static final List<String>
            EXCLUDE_URL =
            List.of("/static/**",
                    "/favicon.ico",
                    "/admin",
                    "/admin/authentication",
                    "/admin/refresh",
                    "/admin/join",
                    "/admin/join/**",
                    "/admin/loginView",
                    "/admin/login");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, java.io.IOException {
        // jwt local storage 사용 시 해당 코드를 사용하여 header에서 토큰을 받아오도록 함
        final String token = request.getHeader("Authorization");

        String id = null;

        String jwtToken = null;


        // Bearer token인 경우 JWT 토큰 유효성 검사 진행
        if (token != null && token.startsWith("Bearer ")) {
            jwtToken = token.substring(7);
            try {
                id = jwtTokenProvider.getUsernameFromToken(jwtToken);
            } catch (MalformedJwtException e) {
                log.error("Invalid JWT token: {}", e.getMessage());
            } catch (ExpiredJwtException e) {
                log.error("JWT token is expired: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                log.error("JWT token is unsupported: {}", e.getMessage());
            } catch (IllegalArgumentException e) {
                log.error("JWT claims string is empty: {}", e.getMessage());
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }

        // token 검증이 되고 인증 정보가 존재하지 않는 경우 spring security 인증 정보 저장
//        if(id != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//
//            // DB에서 관련 정보 조회
//            // ...
//
////            if(jwtTokenProvider.validateToken(jwtToken)) {
////                UsernamePasswordAuthenticationToken authenticationToken =
////                        new UsernamePasswordAuthenticationToken(adminDTO, null ,adminDTO.getAuthorities());
////
////                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
////            }
//        }

        // accessToken 인증이 되었다면 refreshToken 재발급이 필요한 경우 재발급
        try {
            if (id != null) {
                jwtTokenProvider.reGenerateRefreshToken(id);
            }
        } catch (Exception e) {
            log.error("[JwtRequestFilter] refreshToken 재발급 체크 중 문제 발생 : {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    // Filter에서 제외할 URL 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

}