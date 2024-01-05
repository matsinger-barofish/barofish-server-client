package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.application;

import com.matsinger.barofishserver.domain.basketProduct.application.BasketQueryService;
import com.matsinger.barofishserver.domain.basketProduct.repository.BasketQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("local")
class BasketTastingNoteQueryServiceTest {

    @Autowired private BasketQueryRepository basketQueryRepository;
    @Autowired private BasketQueryService basketQueryService;

    @DisplayName("test")
    @Test
    void test() {
        Optional allByUserIdAndProductId = basketQueryService.findAllByUserIdAndProductId(10, 10);
    }
}