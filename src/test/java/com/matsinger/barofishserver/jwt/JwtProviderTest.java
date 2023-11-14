package com.matsinger.barofishserver.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
class JwtProviderTest {

    @Autowired private JwtProvider jwtProvider;

    @Test
    void isTokenExpired() {
        jwtProvider.isTokenExpired("Bearer eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJVU0VSIiwianRpIjoiNDA3IiwiaWF0IjoxNjk5ODQ1MjI3LCJleHAiOjE3MDA0NTAwMjd9.fQXfHBYE3l3A2aWGLVpnij15eRfWDFdbFhSPXGaoFyptWSiuhaDPy-NIbV2JVHYeqkGQ_BKQkN3SCSyaz8F5ag");
    }
}