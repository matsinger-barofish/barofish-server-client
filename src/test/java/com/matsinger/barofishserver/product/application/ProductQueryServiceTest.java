package com.matsinger.barofishserver.product.application;

import com.matsinger.barofishserver.ProfilesCheck;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.weeksdate.domain.WeeksDate;
import com.matsinger.barofishserver.product.weeksdate.repository.WeeksDateRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
class ProductQueryServiceTest {

    @Autowired private ProductQueryService productQueryService;
    @Autowired private WeeksDateRepository weeksDateRepository;
    @Autowired private ProfilesCheck profilesCheck;

    @DisplayName("오늘, 다음날이 휴일이 아니고, 출고시간 전에 주문한 경우 배송도착에정일은 1일이다.")
    @Test
    void arrivalExpectingDateIs1WhenTodayAndTomorrowIsNotHolidayAndOrderBeForeForwardingTime() {
        // given
        LocalDateTime beforeForwardingTime = LocalDateTime.of(2023, 10, 20, 11, 0);
        LocalDateTime afterForwardingTime = LocalDateTime.of(2023, 10, 20, 13, 0);
        int productForwardingTime = 12;
        int productExpectedArrivalDate = 1;

        String nowDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        List<WeeksDate> weeksDates = List.of(
                new WeeksDate(nowDate, false, ""),
                new WeeksDate(nowDate + 1, false, ""),
                new WeeksDate(nowDate + 2, true, ""),
                new WeeksDate(nowDate + 3, true, ""),
                new WeeksDate(nowDate + 4, true, ""),
                new WeeksDate(nowDate + 5, true, ""));

        Product findProduct = productQueryService.findById(10000);
        // when
        int expectedArrivalDate = productQueryService.calculateExpectedArrivalDate(beforeForwardingTime, findProduct.getForwardingTime(), findProduct.getExpectedDeliverDay(), weeksDates);
        // then
        assertThat(expectedArrivalDate).isEqualTo(1);
    }

    @DisplayName("오늘, 다음날, 2일 후가 휴일이 아니고, 출고시간 이후에 주문한 경우 배송도착에정일은 2일이다.")
    @Test
    void arrivalExpectingDateIs2WhenTodayAndTomorrowIsNotHolidayAndOrderAfterForwardingTime() {
        LocalDateTime beforeForwardingTime = LocalDateTime.of(2023, 10, 20, 11, 0);
        LocalDateTime afterForwardingTime = LocalDateTime.of(2023, 10, 20, 13, 0);

        String nowDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        List<WeeksDate> weeksDates = List.of(
                new WeeksDate(nowDate, false, ""),
                new WeeksDate(nowDate + 1, false, ""),
                new WeeksDate(nowDate + 2, false, ""),
                new WeeksDate(nowDate + 3, true, ""),
                new WeeksDate(nowDate + 4, true, ""),
                new WeeksDate(nowDate + 5, true, ""));

        Product findProduct = productQueryService.findById(10000);
        // when
        int expectedArrivalDate = productQueryService.calculateExpectedArrivalDate(afterForwardingTime, findProduct.getForwardingTime(), findProduct.getExpectedDeliverDay(), weeksDates);
        // then
        assertThat(expectedArrivalDate).isEqualTo(2);
    }

    @DisplayName(
            "평일, 평일, 쉬는날, 평일, 평일" +
            "출고시간 전 주문: 다음날 도착" +
            "출고시간 후 주문: 4일 후 도착"
    )
    @Test
    void arrivalExpectingDateCase2() {
        LocalDateTime beforeForwardingTime = LocalDateTime.of(2023, 10, 20, 11, 0);
        LocalDateTime afterForwardingTime = LocalDateTime.of(2023, 10, 20, 13, 0);

        String nowDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        List<WeeksDate> weeksDates = List.of(
                new WeeksDate(nowDate, false, ""),
                new WeeksDate(nowDate + 1, false, ""),
                new WeeksDate(nowDate + 2, true, ""),
                new WeeksDate(nowDate + 3, false, ""),
                new WeeksDate(nowDate + 4, false, ""),
                new WeeksDate(nowDate + 5, true, ""));

        Product findProduct = productQueryService.findById(10000);
        // when
        int beforeForwardingTimeExpectedArrivalDate = productQueryService.calculateExpectedArrivalDate(beforeForwardingTime, findProduct.getForwardingTime(), findProduct.getExpectedDeliverDay(), weeksDates);
        int afterForwardingTimeExpectedArrivalDate = productQueryService.calculateExpectedArrivalDate(afterForwardingTime, findProduct.getForwardingTime(), findProduct.getExpectedDeliverDay(), weeksDates);
        // then
        assertThat(beforeForwardingTimeExpectedArrivalDate).isEqualTo(1);
        assertThat(afterForwardingTimeExpectedArrivalDate).isEqualTo(4);
    }

    @DisplayName(
            "평일, 평일, 쉬는날, 평일, 쉬는날, 평일, 평일" +
            "출고시간 전 주문: 다음날 도착" +
            "출고시간 후 주문: 6일 후 도착"
    )
    @Test
    void arrivalExpectingDateCase3() {
        LocalDateTime beforeForwardingTime = LocalDateTime.of(2023, 10, 20, 11, 0);
        LocalDateTime afterForwardingTime = LocalDateTime.of(2023, 10, 20, 13, 0);

        String nowDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        List<WeeksDate> weeksDates = List.of(
                new WeeksDate(nowDate, false, ""),
                new WeeksDate(nowDate + 1, false, ""),
                new WeeksDate(nowDate + 2, true, ""),
                new WeeksDate(nowDate + 3, false, ""),
                new WeeksDate(nowDate + 4, true, ""),
                new WeeksDate(nowDate + 5, false, ""),
                new WeeksDate(nowDate + 6, false, ""));

        Product findProduct = productQueryService.findById(10000);
        // when
        int beforeForwardingTimeExpectedArrivalDate = productQueryService.calculateExpectedArrivalDate(beforeForwardingTime, findProduct.getForwardingTime(), findProduct.getExpectedDeliverDay(), weeksDates);
        int afterForwardingTimeExpectedArrivalDate = productQueryService.calculateExpectedArrivalDate(afterForwardingTime, findProduct.getForwardingTime(), findProduct.getExpectedDeliverDay(), weeksDates);
        // then
        assertThat(beforeForwardingTimeExpectedArrivalDate).isEqualTo(1);
        assertThat(afterForwardingTimeExpectedArrivalDate).isEqualTo(6);
    }

    @DisplayName(
            "평일, 공휴일, 평일, 평일" +
            "출고시간 전 주문: 3일 후 도착" +
            "출고시간 후 주문: 3일 후 도착"
    )
    @Test
    void arrivalExpectingDateCase4() {
        LocalDateTime beforeForwardingTime = LocalDateTime.of(2023, 10, 20, 11, 0);
        LocalDateTime afterForwardingTime = LocalDateTime.of(2023, 10, 20, 13, 0);

        String nowDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        List<WeeksDate> weeksDates = List.of(
                new WeeksDate(nowDate, false, ""),
                new WeeksDate(nowDate + 1, true, ""),
                new WeeksDate(nowDate + 2, false, ""),
                new WeeksDate(nowDate + 3, false, ""));

        Product findProduct = productQueryService.findById(10000);
        // when
        int beforeForwardingTimeExpectedArrivalDate = productQueryService.calculateExpectedArrivalDate(beforeForwardingTime, findProduct.getForwardingTime(), findProduct.getExpectedDeliverDay(), weeksDates);
        int afterForwardingTimeExpectedArrivalDate = productQueryService.calculateExpectedArrivalDate(afterForwardingTime, findProduct.getForwardingTime(), findProduct.getExpectedDeliverDay(), weeksDates);
        // then
        assertThat(beforeForwardingTimeExpectedArrivalDate).isEqualTo(3);
        assertThat(afterForwardingTimeExpectedArrivalDate).isEqualTo(3);
    }

    @DisplayName(
            "공휴일, 평일, 평일" +
            "출고시간 전 주문: 2일 후 도착" +
            "출고시간 후 주문: 2일 후 도착"
    )
    @Test
    void arrivalExpectingDateCase5() {
        LocalDateTime beforeForwardingTime = LocalDateTime.of(2023, 10, 20, 11, 0);
        LocalDateTime afterForwardingTime = LocalDateTime.of(2023, 10, 20, 13, 0);

        String nowDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        List<WeeksDate> weeksDates = List.of(
                new WeeksDate(nowDate, true, ""),
                new WeeksDate(nowDate + 1, false, ""),
                new WeeksDate(nowDate + 2, false, ""));

        Product findProduct = productQueryService.findById(10000);
        // when
        int beforeForwardingTimeExpectedArrivalDate = productQueryService.calculateExpectedArrivalDate(beforeForwardingTime, findProduct.getForwardingTime(), findProduct.getExpectedDeliverDay(), weeksDates);
        int afterForwardingTimeExpectedArrivalDate = productQueryService.calculateExpectedArrivalDate(afterForwardingTime, findProduct.getForwardingTime(), findProduct.getExpectedDeliverDay(), weeksDates);
        // then
        assertThat(beforeForwardingTimeExpectedArrivalDate).isEqualTo(2);
        assertThat(afterForwardingTimeExpectedArrivalDate).isEqualTo(2);
    }

    @DisplayName(
            "공휴일, 평일, 공휴일, 평일, 평일" +
            "출고시간 전 주문: 4일 후 도착" +
            "출고시간 후 주문: 4일 후 도착"
    )
    @Test
    void arrivalExpectingDateCase6() {
        LocalDateTime beforeForwardingTime = LocalDateTime.of(2023, 10, 20, 11, 0);
        LocalDateTime afterForwardingTime = LocalDateTime.of(2023, 10, 20, 13, 0);

        String nowDate = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        List<WeeksDate> weeksDates = List.of(
                new WeeksDate(nowDate, true, ""),
                new WeeksDate(nowDate + 1, false, ""),
                new WeeksDate(nowDate + 2, true, ""),
                new WeeksDate(nowDate + 3, false, ""),
                new WeeksDate(nowDate + 4, false, ""));

        Product findProduct = productQueryService.findById(10000);
        // when
        int beforeForwardingTimeExpectedArrivalDate = productQueryService.calculateExpectedArrivalDate(beforeForwardingTime, findProduct.getForwardingTime(), findProduct.getExpectedDeliverDay(), weeksDates);
        int afterForwardingTimeExpectedArrivalDate = productQueryService.calculateExpectedArrivalDate(afterForwardingTime, findProduct.getForwardingTime(), findProduct.getExpectedDeliverDay(), weeksDates);
        // then
        assertThat(beforeForwardingTimeExpectedArrivalDate).isEqualTo(4);
        assertThat(afterForwardingTimeExpectedArrivalDate).isEqualTo(4);
    }
}