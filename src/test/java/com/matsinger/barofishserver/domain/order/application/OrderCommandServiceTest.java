package com.matsinger.barofishserver.domain.order.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class OrderCommandServiceTest {

    @DisplayName("")
    @Test
    void test() {
        // given
        double settlementRate = 13.2;
        double settlementRate2 = settlementRate / 100.;
        double price = settlementRate2 * 10000;
        // when

        // then
    }
}