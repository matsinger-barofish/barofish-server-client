package com.matsinger.barofishserver.domain.product.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("local")
class ProductQueryRepositoryTest {

    @DisplayName("")
    @Test
    void test() {
        // given
        Map<Integer, List<Integer>> map = new HashMap<>();

        // when
        List<Integer> existingValue = map.getOrDefault(1, new ArrayList<>());
        existingValue.add(1);
        map.put(
                1,
                existingValue
        );
        List<Integer> existingValue2 = map.getOrDefault(1, new ArrayList<>());
        existingValue2.add(2);
        map.put(
                1,
                existingValue2
        );
        // then
        System.out.println("map.get(1) = " + map.get(1));
    }
}