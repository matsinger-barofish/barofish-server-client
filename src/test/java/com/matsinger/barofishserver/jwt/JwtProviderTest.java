package com.matsinger.barofishserver.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void generateAccessToken() {
        // given
        String accessToken = jwtProvider.generateAccessToken("1", TokenAuthType.USER);
        // when

        // then
    }

    @Test
    void isTokenExpired() {
//        jwtProvider.isTokenExpired("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJVU0VSIiwianRpIjoiNjQiLCJpYXQiOjE2OTk4NDQ5OTksImV4cCI6MTcwMDQ0OTc5OX0.69zcWNbOKUkSSwsVOVJorNfCbG3zF7U9489sXPmf0w6J4OOcBfvdbF1oQCccf2E8ujGfs-O8l5cKu75U-6i78A");
        jwtProvider.isTokenExpired("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJVU0VSIiwianRpIjoiMTAwMDkiLCJpYXQiOjE3MDAwMzI0MzYsImV4cCI6MTcwMDYzNzIzNn0.7M_IQwo5JWl464tcRwT9jLV2VL1mJ7kXjgu1gPQ-UypF0KAwnrcocKOlvaud9JO_XSXCc4LGlsU3fujF_CpiSQ");
    }
}