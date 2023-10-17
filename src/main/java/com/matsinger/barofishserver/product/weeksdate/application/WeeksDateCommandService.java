package com.matsinger.barofishserver.product.weeksdate.application;

import com.matsinger.barofishserver.product.weeksdate.dto.Holiday;
import com.matsinger.barofishserver.product.weeksdate.domain.WeeksDate;
import com.matsinger.barofishserver.product.weeksdate.repository.WeeksDateRepository;
import com.matsinger.barofishserver.product.weeksdate.repository.WeeksDateRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class WeeksDateCommandService {

    private final WeeksDateQueryService weeksDateQueryService;
    private final WeeksDateRepository weeksDateRepository;
    private final WeeksDateRepositoryImpl weeksDateRepositoryImpl;

    @Transactional
    public void saveThisAndNextWeeksDate(LocalDate date) throws IOException {
        String[] dateElement = date.toString().split("-");
        if (dateElement[1].startsWith("0")) {
            dateElement[1] = dateElement[1].replace(String.valueOf('0'), "");
        }
        if (dateElement[2].startsWith("0")) {
            dateElement[2] = dateElement[2].replace(String.valueOf('0'), "");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.valueOf(dateElement[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(dateElement[1]) - 1); // 캘린더 월 인덱스는 0부터 시작해서 -1 해줘야함
        calendar.set(Calendar.DATE, Integer.valueOf(dateElement[2]));

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        String paddedCurrentMonth = StringUtils.leftPad(String.valueOf(currentMonth), 2, "0");
        String paddedNextMonth = StringUtils.leftPad(String.valueOf(currentMonth + 1), 2, "0");

        List<Holiday> koreanHolidays = weeksDateQueryService.getKoreanHolidays(String.valueOf(currentYear), paddedCurrentMonth);
        List<Holiday> nextMonthHolidays = weeksDateQueryService.getKoreanHolidays(String.valueOf(currentYear), paddedNextMonth);


        if (currentMonth == 12) {
            nextMonthHolidays = weeksDateQueryService.getKoreanHolidays(String.valueOf(currentYear + 1), "01");
            koreanHolidays.addAll(nextMonthHolidays);
        }

        koreanHolidays.addAll(nextMonthHolidays);


        List<WeeksDate> weeksDates = new ArrayList<>();
        for (int i=0; i<14; i++) {
            WeeksDate weeksDate = createWeeksDate(calendar, koreanHolidays);
            weeksDates.add(weeksDate);

            calendar.add(Calendar.DATE, 1);
        }

        weeksDateRepository.saveAll(weeksDates);
//        compareExistingDateAndSave(date, date.plusDays(14), weeksDates);
    }

//    private void compareExistingDateAndSave(LocalDate startDate, LocalDate endDate, List<WeeksDate> weeksDates) {
//        List<WeeksDate> existingDates = weeksDateRepositoryImpl.getWeeksDate(startDate, endDate);
//
//        for (WeeksDate existingDate : existingDates) {
//
//            for (WeeksDate weeksDate : weeksDates) {
//                if (weeksDate.getDate() == weeksDate.getDate()) {
//                    weeksDateRepository.sa
//                }
//            }
//
//        }
//    }

    private WeeksDate createWeeksDate(Calendar calendar, List<Holiday> koreanHolidays) {
        int dayOfTheWeek = calendar.get(Calendar.DAY_OF_WEEK); // 일 = 1, ... , 토 = 7

        LocalDate localDate = LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE));

        String date = localDate.toString().replace("-", "");

        String dateDescription = "";
        boolean isDeliveryCompanyHoliday = false;
        if (dayOfTheWeek == 1) {
            dateDescription = "일";
            isDeliveryCompanyHoliday = true;
        }
        if (dayOfTheWeek == 2) {
            dateDescription = "월";
        }
        if (dayOfTheWeek == 3) {
            dateDescription = "화";
        }
        if (dayOfTheWeek == 4) {
            dateDescription = "수";
        }
        if (dayOfTheWeek == 5) {
            dateDescription = "목";
        }
        if (dayOfTheWeek == 6) {
            dateDescription = "금";
        }
        if (dayOfTheWeek == 7) {
            dateDescription = "토";
        }

        if (!koreanHolidays.isEmpty()) {
            for (Holiday holiday : koreanHolidays) {
                if (holiday.sameDate(date)) {
                    dateDescription = holiday.getDateName();
                    isDeliveryCompanyHoliday = true;
                }
            }
        }

        return WeeksDate.builder()
                .date(date.toString().replace("-", ""))
                .isDeliveryCompanyHoliday(isDeliveryCompanyHoliday)
                .description(dateDescription)
                .build();
    }
}
