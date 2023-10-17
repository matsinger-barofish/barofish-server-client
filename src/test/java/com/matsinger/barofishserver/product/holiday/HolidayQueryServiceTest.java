package com.matsinger.barofishserver.product.holiday;

import com.matsinger.barofishserver.product.weeksdate.application.WeeksDateQueryService;
import com.matsinger.barofishserver.product.weeksdate.Holidays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class HolidayQueryServiceTest {

    @Autowired
    private WeeksDateQueryService weeksDateQueryService;

    @DisplayName("sample test")
    @Test
    void testMethodNameHere() throws IOException {
        // given
        Holidays holidays = weeksDateQueryService.getKoreanHolidays("2023", "10");
        System.out.println(holidays.toString());

        // when
        // then
    }

    @DisplayName("오늘이 12월 마지막주이면 내년 1월 첫째주 공휴일 데이터를 불러올 수 있다.")
    @Test
    void apiConditionTest() throws IOException {
        weeksDateQueryService.saveThisAndNextWeeksDate();
    }
}