package com.matsinger.barofishserver.product.weeksdate;

import com.matsinger.barofishserver.product.weeksdate.application.WeeksDateCommandService;
import com.matsinger.barofishserver.product.weeksdate.application.WeeksDateQueryService;
import com.matsinger.barofishserver.product.weeksdate.dto.Holiday;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
//@Transactional
class WeeksDateApplicationTest {

    @Autowired
    private WeeksDateQueryService weeksDateQueryService;
    @Autowired private WeeksDateCommandService weeksDateCommandService;

    @DisplayName("sample test")
    @Test
    void testMethodNameHere() throws IOException {
        // given
        List<Holiday> holidays = weeksDateQueryService.getKoreanHolidays("2023", "10");
        System.out.println(holidays.toString());

        // when
        // then
    }

    @DisplayName("2023-01-01 형식으로 된 LocalDate를 Calendar의 날짜를 세팅할 수 있다.")
    @Test
    void convertLocalDateToCalendar() throws IOException {
//        weeksDateCommandService.saveThisAndNextWeeksDate();

        LocalDate date = LocalDate.now();

        String[] dateElement = date.toString().split("-");
        if (dateElement[1].startsWith("0")) {
            dateElement[1] = dateElement[1].replace(String.valueOf('0'), "");
        }
        if (dateElement[2].startsWith("0")) {
            dateElement[2] = dateElement[2].replace(String.valueOf('0'), "");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.valueOf(dateElement[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(dateElement[1]));
        calendar.set(Calendar.DATE, Integer.valueOf(dateElement[2]));
    }
}