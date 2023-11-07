package com.matsinger.barofishserver.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Configuration
public class JwtProvider {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${spring.jwt.secret}")
    private String secret;

    /**
     *     1000: 1초
     *     60: 1분 (60초)
     *     60: 1시간 (60분)
     *     24: 1일 (24시간)
     */
    public static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * 24;

    // token으로 사용자 id 조회
    public Integer getIdFromToken(String token) {
        String claimFromToken = getClaimFromToken(token, Claims::getId);
        if (claimFromToken == null) {
            return null;
        }
        return Integer.valueOf(claimFromToken);
    }

    public TokenAuthType getTypeFromToken(String token) {
        String issuer = getClaimFromToken(token, Claims::getIssuer);
        if (issuer == null) {
            return TokenAuthType.ALLOW;
        }
        return switch (issuer) {
            case "USER" -> TokenAuthType.USER;
            case "ADMIN" -> TokenAuthType.ADMIN;
            case "PARTNER" -> TokenAuthType.PARTNER;
            default -> TokenAuthType.ALLOW;
        };
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
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // 토큰 만료일자 조회
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // id를 입력받아 accessToken 생성
    public String generateAccessToken(String id, TokenAuthType issuer) {
        return generateAccessToken(id, issuer, new HashMap<>());
    }

    // id, 속성정보를 이용해 accessToken 생성
    public String generateAccessToken(String id, TokenAuthType issuer, Map<String, Object> claims) {
        String
                issuerString =
                issuer.equals(TokenAuthType.USER) ? "USER" : issuer.equals(TokenAuthType.PARTNER) ? "PARTNER" : "ADMIN";
        return doGenerateAccessToken(id, issuerString, claims);
    }

    // JWT accessToken 생성
    private String doGenerateAccessToken(String id, String issuer, Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setIssuer(issuer).setId(id).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(
                new Date(System.currentTimeMillis() +
                        JWT_TOKEN_VALIDITY * 7)).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    // id를 입력받아 accessToken 생성
    public String generateRefreshToken(String id, TokenAuthType issuer) {

        String
                issuerString =
                issuer.equals(TokenAuthType.USER) ? "USER" : issuer.equals(TokenAuthType.PARTNER) ? "PARTNER" : "ADMIN";
        return doGenerateRefreshToken(id, issuerString);
    }

    // JWT accessToken 생성
    private String doGenerateRefreshToken(String id, String issuer) {

        return Jwts.builder().setId(id).setExpiration(new Date(System.currentTimeMillis() +
                    JWT_TOKEN_VALIDITY * 31))
           .setIssuedAt(new Date(System.currentTimeMillis())).signWith(SignatureAlgorithm.HS512,
                    secret).compact();
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
        Map<String, String> tokens = new HashMap<>();

        String
                accessToken =
                Jwts.builder().setClaims(claims).setId(id).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(
                            new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 7))// 1시간
                    .signWith(SignatureAlgorithm.HS512, secret).compact();

        String
                refreshToken =
                Jwts.builder().setId(id).setExpiration(new Date(System.currentTimeMillis() +
                            JWT_TOKEN_VALIDITY * 31)) // 5시간
                    .setIssuedAt(new Date(System.currentTimeMillis())).signWith(SignatureAlgorithm.HS512,
                            secret).compact();

        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    // 토근 검증
    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("토큰 정보가 유효하지 않습니다.");
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw new JwtException("토큰 정보가 만료되었습니다. 다시 로그인해 주세요.");
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            throw new JwtException("지원하지 않는 토큰 정보입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            throw new JwtException("토큰 정보가 없습니다. 다시 로그인해 주세요.");
        }
    }
}
