package com.matsinger.barofishserver.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class JwtProviderTest {

    @Autowired private JwtProvider jwtProvider;

    @Test
    void generateAccessToken() {
        // given
        String accessToken = jwtProvider.generateAccessToken("1", TokenAuthType.USER);
        System.out.println("accessToken = " + accessToken);
        // when

        // then
    }

    @Test
    void isTokenExpired() {
//        jwtProvider.isTokenExpired("Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJVU0VSIiwianRpIjoiMTk5IiwiaWF0IjoxNjk5MjU0MTAzLCJleHAiOjE2OTk4NTg5MDN9.cqZBu1MzWkkync1piQRe6Lo--DEyAKS7-xqIQma1dBBTImM-urDlwfzqYL-mUn0JSl4J10EIbHv2a1mlndw_9A");
        jwtProvider.isTokenExpired("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJVU0VSIiwianRpIjoiMSIsImlhdCI6MTY5OTk1MTcyOSwiZXhwIjoxNjk5OTUxNzM2fQ.2BZMj-RU15wnYjBPiwtuRIGP4uQr0nxDYZxv_Kd_BLGLCF7Uw_ISd64iDLLhRN9SJbEvS-s7xPFHaQAGPcVBHQ");
    }
}