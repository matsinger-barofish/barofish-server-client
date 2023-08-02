package com.matsinger.barofishserver.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest
class AdminServiceTest {

    @DisplayName("관리자 비밀번호 생성용")
    @Test
    void test() {
        System.out.println(BCrypt.hashpw("qwer123$", BCrypt.gensalt()));
    }
}