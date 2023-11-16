package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.application;

import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.domain.BasketTastingNote;
import com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.repository.BasketTastingNoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class BasketTastingNoteCommandServiceTest {

    @Autowired BasketTastingNoteCommandService basketTastingNoteCommandService;
    @Autowired BasketTastingNoteRepository basketTastingNoteRepository;

    @DisplayName("테이스팅노트 바구니에 테이스팅노트를 저장할 수 있다.")
    @Test
    void addTastingNoteTest() {
        // given // when
        basketTastingNoteCommandService.addTastingNote(10000, 10000);
        // then
        assertThat(basketTastingNoteRepository.findAllByUserId(10000).get(0).getUser().getId()).isEqualTo(10000);
        assertThat(basketTastingNoteRepository.findAllByUserId(10000).get(0).getProductId()).isEqualTo(10000);
    }

    @DisplayName("테이스팅노트 바구니에서 테이스팅노트를 삭제할 수 있다.")
    @Test
    void deleteTastingNoteTest() {
        // given
        basketTastingNoteCommandService.addTastingNote(10000, 10000);
        // when
        basketTastingNoteCommandService.deleteTastingNote(10000, 10000);
        // then
        List<BasketTastingNote> basketTastingNotes = basketTastingNoteRepository.findAllByUserId(10000);
        assertThat(basketTastingNotes.isEmpty()).isTrue();
    }
}