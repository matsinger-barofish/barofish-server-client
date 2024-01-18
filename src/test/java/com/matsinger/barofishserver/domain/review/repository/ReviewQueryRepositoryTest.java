package com.matsinger.barofishserver.domain.review.repository;

import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class ReviewQueryRepositoryTest {

    @Autowired private ReviewQueryRepository reviewQueryRepository;
    @Autowired private ProductQueryService productQueryService;

}