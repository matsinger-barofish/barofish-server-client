package com.matsinger.barofishserver.product.holiday;

import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class HolidayQueryServiceTest {

    @Autowired private HolidayQueryService holidayQueryService;

    @DisplayName("sample test")
    @Test
    void testMethodNameHere() throws IOException {
        // given
        Holidays holidays = holidayQueryService.getOpenDataAnniversaryInfoResponse("2023", "10", PageRequest.of(0, 28));

        // when
        // then
    }
}