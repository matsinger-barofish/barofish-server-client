package com.matsinger.barofishserver.domain.product.weeksdate.application;

import com.matsinger.barofishserver.domain.product.weeksdate.dto.Holiday;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;


@SpringBootTest
@ActiveProfiles("local")
class WeeksDateCommandServiceTest {

    @Autowired private WeeksDateQueryService weeksDateQueryService;

    @DisplayName("")
    @Test
    void test() throws IOException {
        // given
        List<Holiday> koreanHolidays = weeksDateQueryService.getKoreanHolidays("2024", "02");
        // when

        // then
    }
}