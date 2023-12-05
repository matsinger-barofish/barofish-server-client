package com.matsinger.barofishserver.domain.review.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class ReviewRepositoryTest {

    @Autowired private ReviewRepository reviewRepository;

    @DisplayName("test")
    @Test
    void test() {
        // given
        reviewRepository.countAllByProductIdAndIsDeleted(10000, false);
        // when

        // then
    }
}