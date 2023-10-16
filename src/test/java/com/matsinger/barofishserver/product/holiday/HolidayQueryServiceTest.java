package com.matsinger.barofishserver.product.holiday;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class HolidayQueryServiceTest {

    @Autowired
    private HolidayQueryService holidayQueryService;

    @DisplayName("sample test")
    @Test
    void testMethodNameHere() throws IOException {
        // given
        Holidays holidays = holidayQueryService.getOpenDataHolidayInfoResponse("2023", "10", PageRequest.of(0, 28));
        System.out.println(holidays.toString());

        // when
        // then
    }

    @DisplayName("오늘이 12월 마지막주이면 내년 1월 첫째주 공휴일 데이터를 불러올 수 있다.")
    @Test
    void apiConditionTest() {
        // given
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date();
        calendar.setTime(currentDate);
//        calendar.add(Calendar.DATE, 15);

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        int dayOfThisWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 만약 현재 월이 12월이고 현재 주가 마지막 주인 경우
        if (currentMonth == Calendar.DECEMBER && currentWeek == calendar.getActualMaximum(Calendar.WEEK_OF_YEAR)) {
            // 내년 1월의 첫 주 데이터를 불러오는 로직을 여기에 추가합니다.
            int nextYear = currentYear + 1;

        }

        calendar.add(Calendar.DAY_OF_WEEK, 7);

        int nextWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        int dayOfNextWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // when

        // then
    }
}