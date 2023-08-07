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

    @DisplayName("비밀번호 생성용")
    @Test
    void test() {
        // givend
        String password = "qwer123$";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        // when

        // then
        System.out.println("hashedPassword = " + hashedPassword);
    }
}