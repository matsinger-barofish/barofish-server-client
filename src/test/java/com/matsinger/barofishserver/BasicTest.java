package com.matsinger.barofishserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

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

    @DisplayName("sample test")
    @Test
    void listTest() {
        // given
        List<Integer> testList = List.of(1, 2, 3);
        int totalOriginPrice = 10000;
        // when
        int sum = testList.subList(0, testList.size() - 1).stream().mapToInt(v -> v).sum();
        testList.set(testList.size() - 1, totalOriginPrice - sum);
        // then
        System.out.println("testList = " + testList);
    }
}
