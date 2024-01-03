package com.matsinger.barofishserver.domain.store.repository;

import com.matsinger.barofishserver.domain.store.dto.StoreRecommendInquiryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
class StoreQueryRepositoryTest {

    @Autowired private StoreQueryRepository storeQueryRepository;

    @DisplayName("")
    @Test
    void test() {
        // given
        List<StoreRecommendInquiryDto> storeRecommendInquiryDtos = storeQueryRepository.selectRecommendStoreWithReview(
                PageRequest.of(1, 10),
                "",
                84);
        // when

        // then
    }
}