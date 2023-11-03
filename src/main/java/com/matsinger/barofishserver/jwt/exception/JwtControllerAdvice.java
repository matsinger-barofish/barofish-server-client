package com.matsinger.barofishserver.jwt.exception;

import com.matsinger.barofishserver.utils.CustomResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = {"com.matsinger.barofishserver"})
@RequiredArgsConstructor
public class JwtControllerAdvice {

    @ExceptionHandler(value = {
            JwtException.class,
            ExpiredJwtException.class})
    public ResponseEntity<CustomResponse<Object>> getJwtExpiredMessage(Exception e) {
        e.printStackTrace();

        CustomResponse customResponse = new CustomResponse();
        customResponse.setIsSuccess(false);
        customResponse.setErrorMsg("토큰 정보가 만료되었습니다. 다시 로그인해 주세요.");
        return ResponseEntity.ok(customResponse);
    }
}
