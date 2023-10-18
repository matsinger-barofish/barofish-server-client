package com.matsinger.barofishserver.product.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
class ProductQueryServiceTest {

    @DisplayName("sample test")
    @Test
    void testMethodNameHere() {
        // given
        int monthValue = LocalDate.now().getMonthValue();
        System.out.println("month = " + monthValue);
        // when

        // then
    }
}