package com.matsinger.barofishserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

@ActiveProfiles("local")
@SpringBootTest
public class BasicTest {

    @DisplayName("sample test")
    @Test
    void testMethodNameHere() {
        // given
        System.out.println(LocalDateTime.now());
        // when

        // then
    }
}
