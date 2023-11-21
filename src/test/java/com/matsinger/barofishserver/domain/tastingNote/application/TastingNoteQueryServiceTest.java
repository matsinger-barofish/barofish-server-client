package com.matsinger.barofishserver.domain.tastingNote.application;

import com.matsinger.barofishserver.domain.tastingNote.domain.*;
import com.matsinger.barofishserver.domain.tastingNote.dto.ProductTastingNoteInquiryDto;
import com.matsinger.barofishserver.domain.tastingNote.dto.ProductTastingNoteResponse;
import com.matsinger.barofishserver.domain.tastingNote.repository.TastingNoteRepository;
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
class TastingNoteQueryServiceTest {

    @Autowired
    private TastingNoteRepository tastingNoteRepository;
    @Autowired
    private TastingNoteQueryService tastingNoteQueryService;

    @DisplayName("테이스팅노트 점수 계산은 소수점 둘째자리에서 반올림한다.")
    @Test
    void roundScoresToSecondDecimalPlace() {
        // given
        Double score1 = 3.333;
        Double score2 = 3.666;
        // when
        double roundedResult1 = Math.round(score1 * 10) / 10.0;
        double roundedResult2 = Math.round(score2 * 10) / 10.0;
        // then
        assertThat(roundedResult1).isEqualTo(3.3);
        assertThat(roundedResult2).isEqualTo(3.7);
    }

    @DisplayName("상품의 테이스팅노트를 비교할 수 있다.")
    @Test
    void compareProductTastingNotes() {
        // given
        // 상품1
        int productId1 = 10000;
        int productId2 = 10001;
        tastingNoteRepository.save(TastingNote.builder()
                .orderProductInfoId(10000)
                .productId(productId1)
                .userId(10000)
                .oily(1)
                .taste2(1)
                .taste3(1)
                .taste4(1)
                .taste5(1)
                .tendernessSoftness(1)
                .texture2(1)
                .texture3(1)
                .texture4(1)
                .texture5(1)
                .build());

        tastingNoteRepository.save(TastingNote.builder()
                .orderProductInfoId(10001)
                .productId(productId1)
                .userId(10000)
                .oily(5)
                .taste2(5)
                .taste3(5)
                .taste4(5)
                .taste5(5)
                .tendernessSoftness(5)
                .texture2(5)
                .texture3(5)
                .texture4(5)
                .texture5(5)
                .build());

        tastingNoteRepository.save(TastingNote.builder()
                .orderProductInfoId(10002)
                .productId(productId1)
                .userId(10000)
                .oily(5)
                .taste2(5)
                .taste3(5)
                .taste4(5)
                .taste5(5)
                .tendernessSoftness(5)
                .texture2(5)
                .texture3(5)
                .texture4(5)
                .texture5(5)
                .build());
        // 상품2
        tastingNoteRepository.save(TastingNote.builder()
                .orderProductInfoId(10000)
                .productId(productId2)
                .userId(10000)
                .oily(1)
                .taste2(1)
                .taste3(1)
                .taste4(1)
                .taste5(1)
                .tendernessSoftness(1)
                .texture2(1)
                .texture3(1)
                .texture4(1)
                .texture5(1)
                .build());

        tastingNoteRepository.save(TastingNote.builder()
                .orderProductInfoId(10001)
                .productId(productId2)
                .userId(10000)
                .oily(5)
                .taste2(5)
                .taste3(5)
                .taste4(5)
                .taste5(5)
                .tendernessSoftness(5)
                .texture2(5)
                .texture3(5)
                .texture4(5)
                .texture5(5)
                .build());

        tastingNoteRepository.save(TastingNote.builder()
                .orderProductInfoId(10002)
                .productId(productId2)
                .userId(10000)
                .oily(5)
                .taste2(5)
                .taste3(5)
                .taste4(5)
                .taste5(5)
                .tendernessSoftness(5)
                .texture2(5)
                .texture3(5)
                .texture4(5)
                .texture5(5)
                .build());
        // when
        List<ProductTastingNoteResponse> tastingNotes = tastingNoteQueryService.compareTastingNotes(List.of(productId1, productId2));
        // then
        assertThat(tastingNotes.size()).isEqualTo(2);
    }

    @DisplayName("맛, 식감을 점수 순으로 정렬할 수 있다.")
    @Test
    void sortByScoreTest() {
        // given
        ProductTastingNoteInquiryDto procutTastingNote = ProductTastingNoteInquiryDto.builder()
                .taste1("taste1")
                .taste1Score(1.0)
                .taste2("taste2")
                .taste2Score(2.0)
                .taste3("taste3")
                .taste3Score(3.0)
                .taste4("taste4")
                .taste4Score(4.0)
                .taste5("taste5")
                .taste5Score(5.0)
                .texture1("texture1")
                .texture1Score(1.0)
                .texture2("texture2")
                .texture2Score(2.0)
                .texture3("texture3")
                .texture3Score(3.0)
                .texture4("texture4")
                .texture4Score(4.0)
                .texture5("texture5")
                .texture5Score(5.0)
                .build();

        // when
        TastingNoteTastes tastes = procutTastingNote.getTastes();
        tastes.sortByScore();
        TastingNoteTextures textures = procutTastingNote.getTextures();
        textures.sortByScore();

        // then
        TastingNoteTaste highestScoreTaste = tastes.getTasteInTheOrderOfTheHighestScore(1);
        assertThat(highestScoreTaste.getScore()).isEqualTo(5.0);
        assertThat(highestScoreTaste.getTaste()).isEqualTo("taste5");

        TastingNoteTexture highestScoreTexture = textures.getTextureInTheOrderOfTheHighestScore(1);
        assertThat(highestScoreTexture.getScore()).isEqualTo(5.0);
        assertThat(highestScoreTexture.getTexture()).isEqualTo("texture5");
    }

    @DisplayName("상품의 테이스팅노트를 맛 점수 내림차순, 식감 점수 내림차순으로 가져올 수 있다.")
    @Test
    void getTastingNoteInOrderOfHighestScore() {
        // given
        int productId1 = 10000;
        tastingNoteRepository.save(TastingNote.builder()
                .orderProductInfoId(10000)
                .productId(productId1)
                .userId(10000)
                .oily(5)
                .taste2(4)
                .taste3(3)
                .taste4(2)
                .taste5(1)
                .tendernessSoftness(5)
                .texture2(4)
                .texture3(3)
                .texture4(2)
                .texture5(1)
                .build());

        tastingNoteRepository.save(TastingNote.builder()
                .orderProductInfoId(10001)
                .productId(productId1)
                .userId(10000)
                .oily(1)
                .taste2(2)
                .taste3(3)
                .taste4(4)
                .taste5(5)
                .tendernessSoftness(1)
                .texture2(2)
                .texture3(3)
                .texture4(4)
                .texture5(5)
                .build());

        tastingNoteRepository.save(TastingNote.builder()
                .orderProductInfoId(10002)
                .productId(productId1)
                .userId(10000)
                .oily(1)
                .taste2(1)
                .taste3(5)
                .taste4(1)
                .taste5(1)
                .tendernessSoftness(1)
                .texture2(1)
                .texture3(5)
                .texture4(1)
                .texture5(1)
                .build());

        // when
        ProductTastingNoteResponse tastingNoteResponse = tastingNoteQueryService.getTastingNoteInfo(10000);

        // then
        assertThat(tastingNoteResponse.getTastes().get(0).getScore()).isEqualTo(3.7);
        assertThat(tastingNoteResponse.getTastes().get(0).getTaste()).isEqualTo("taste3");
        assertThat(tastingNoteResponse.getTextures().get(0).getScore()).isEqualTo(3.7);
        assertThat(tastingNoteResponse.getTextures().get(0).getTexture()).isEqualTo("texture3");
    }
}