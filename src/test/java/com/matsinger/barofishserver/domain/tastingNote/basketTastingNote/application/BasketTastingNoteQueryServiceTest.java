package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.application;

import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.repository.BasketTastingNoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("local")
class BasketTastingNoteQueryServiceTest {

    @Autowired private BasketTastingNoteRepository basketTastingNoteRepository;

    @DisplayName("test")
    @Test
    void test() {
        basketTastingNoteRepository.existsByUserIdAndProductId(10000, 10000);
    }
}