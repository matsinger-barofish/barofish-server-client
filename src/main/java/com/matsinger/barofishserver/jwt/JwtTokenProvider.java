package com.matsinger.barofishserver.jwt;

import io.jsonwebtoken.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Configuration
public class JwtTokenProvider {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${spring.jwt.secret}")
    private String secret;

    // 1시간 단위
    public static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60;

    // token으로 사용자 id 조회
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getId);
    }

    public String getTypeFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuer);
    }

    // token으로 사용자 속성정보 조회
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 모든 token에 대한 사용자 속성정보 조회
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // 토근 만료 여부 체크
	/*
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	*/

    // 토큰 만료일자 조회
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // id를 입력받아 accessToken 생성
    public String generateAccessToken(String id, String issuer) {
        return generateAccessToken(id, issuer, new HashMap<>());
    }

    // id, 속성정보를 이용해 accessToken 생성
    public String generateAccessToken(String id, String issuer, Map<String, Object> claims) {
        return doGenerateAccessToken(id, issuer, claims);
    }

    // JWT accessToken 생성
    private String doGenerateAccessToken(String id, String issuer, Map<String, Object> claims) {
        String
                accessToken =
                Jwts.builder().setClaims(claims).setIssuer(issuer).setId(id).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(
                            new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1))// 1시간
                    .signWith(SignatureAlgorithm.HS512, secret).compact();

        return accessToken;
    }

    // id를 입력받아 accessToken 생성
    public String generateRefreshToken(String id) {
        return doGenerateRefreshToken(id);
    }

    // JWT accessToken 생성
    private String doGenerateRefreshToken(String id) {
        String
                refreshToken =
                Jwts.builder().setId(id).setExpiration(new Date(System.currentTimeMillis() +
                            JWT_TOKEN_VALIDITY * 5)) // 5시간
                    .setIssuedAt(new Date(System.currentTimeMillis())).signWith(SignatureAlgorithm.HS512,
                            secret).compact();

        return refreshToken;
    }

    // id를 입력받아 accessToken, refreshToken 생성
    public Map<String, String> generateTokenSet(String id) {
        return generateTokenSet(id, new HashMap<>());
    }

    // id, 속성정보를 이용해 accessToken, refreshToken 생성
    public Map<String, String> generateTokenSet(String id, Map<String, Object> claims) {
        return doGenerateTokenSet(id, claims);
    }

    // JWT accessToken, refreshToken 생성
    private Map<String, String> doGenerateTokenSet(String id, Map<String, Object> claims) {
        Map<String, String> tokens = new HashMap<String, String>();

        String
                accessToken =
                Jwts.builder().setClaims(claims).setId(id).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(
                            new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1))// 1시간
                    .signWith(SignatureAlgorithm.HS512, secret).compact();

        String
                refreshToken =
                Jwts.builder().setId(id).setExpiration(new Date(System.currentTimeMillis() +
                            JWT_TOKEN_VALIDITY * 5)) // 5시간
                    .setIssuedAt(new Date(System.currentTimeMillis())).signWith(SignatureAlgorithm.HS512,
                            secret).compact();

        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    // JWT refreshToken 만료체크 후 재발급
    public Boolean reGenerateRefreshToken(String id) throws Exception {
        log.info("[reGenerateRefreshToken] refreshToken 재발급 요청");
        // DB에서 정보 조회
        // ...


        // refreshToken 정보가 존재하지 않는 경우
//        if(rDTO == null) {
//            log.info("[reGenerateRefreshToken] refreshToken 정보가 존재하지 않습니다.");
//            return false;
//        }

        // refreshToken 만료 여부 체크
        try {
//            String refreshToken = rDTO.getRefreshToken().substring(7);
//            Jwts.parser().setSigningKey(secret).parseClaimsJws(refreshToken);
            log.info("[reGenerateRefreshToken] refreshToken이 만료되지 않았습니다.");
            return true;
        }
        // refreshToken이 만료된 경우 재발급
        catch (ExpiredJwtException e) {
//            rDTO.setRefreshToken("Bearer " + generateRefreshToken(id));
            // ... DB에서 refreshToken 정보 수정
            log.info("[reGenerateRefreshToken] refreshToken 재발급 완료 : {}", "Bearer " + generateRefreshToken(id));
            return true;
        }
        // 그 외 예외처리
        catch (Exception e) {
            log.error("[reGenerateRefreshToken] refreshToken 재발급 중 문제 발생 : {}", e.getMessage());
            return false;
        }
    }

    // 토근 검증
    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
