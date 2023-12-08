package com.matsinger.barofishserver.domain.review.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class ReviewRepositoryTest {

    @Autowired private ReviewRepository reviewRepository;
}