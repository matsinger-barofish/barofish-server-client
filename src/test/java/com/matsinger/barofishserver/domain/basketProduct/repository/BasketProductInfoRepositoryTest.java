package com.matsinger.barofishserver.domain.basketProduct.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
class BasketProductInfoRepositoryTest {

    @Autowired BasketQueryRepository basketQueryRepository;

    @DisplayName("")
    @Test
    void test() {
        // given
        List<Integer> ids = List.of(1);
        basketQueryRepository.deleteAllBasketByUserIdAndOptionIds(447, ids);
        // when

        // then
    }
}