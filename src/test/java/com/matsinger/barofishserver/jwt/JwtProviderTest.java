package com.matsinger.barofishserver.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class JwtProviderTest {

    @Autowired private JwtProvider jwtProvider;
    @Autowired private JwtService jwtService;

    @Test
    void generateAccessToken() {
        // given
        String accessToken = jwtProvider.generateAccessToken("1", TokenAuthType.USER);
        System.out.println("accessToken = " + accessToken);
        // when

        // then
    }

    @Test
    void tokenErrorCase() {
        // normal
        jwtProvider.getTypeFromToken("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJVU0VSIiwianRpIjoiNDA3IiwiaWF0IjoxNzAwMTQwMzYxLCJleHAiOjE3MDA3NDUxNjF9.awfw5se4jxgJHOEar4lzN8wo-W_pA5aM68gh88Ki-LfK3HJgh86cnCg9Cz3Sa-lNeQw3godhc08urDU3NGyp7A");

        // not normal to noraml - made by jwt website
        jwtProvider.getIdFromToken("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJVU0VSIiwianRpIjoiNDA3IiwiaWF0IjoxNjk5ODY2MzE1LCJleHAiOjE3MDA0NzExMTV9.VW53x7-xiSKTUjhA8_Lz5pvGHXa4-LE3b8OUEtuEKaBVE8CBVFrBwWL6WT1agOiYS40FPBaOwDn0Ny4NEO_Kfg");

        // NOT NORMAL - error from andriod
        jwtProvider.getIdFromToken("eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJVU0VSIiwianRpIjoiNDA3IiwiaWF0IjoxNjk5ODY2MzE1LCJleHAiOjE3MDA0NzExMTV9.FB0GkZ0V8wnC-iGwFEi73upHBac3zGtw81nTMhv44lF0kWN84_4fv7vntabiKfLK4M_0r591l3kiOUUSe4xF6w");
    }

    @DisplayName("")
    @Test
    void tokenExpirationTest() {
        // given
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJVU0VSIiwianRpIjoiMTAwMDAiLCJpYXQiOjE3MDY1NzQ1ODgsImV4cCI6MTcwNzE3OTM4OH0.aNVRCUB-idm5SW2vICp76Iy8vC44r56kAvHyMakGIo7urynS_hoTJ815as93sRSU2RaVVeYaIetQuY7S1k7CVg";
        Boolean tokenExpired = jwtProvider.isTokenExpired(token);
        // when

        // then
    }
}
