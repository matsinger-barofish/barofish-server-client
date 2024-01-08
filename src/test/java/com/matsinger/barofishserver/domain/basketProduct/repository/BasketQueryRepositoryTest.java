package com.matsinger.barofishserver.domain.basketProduct.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class BasketQueryRepositoryTest {

    @Autowired private BasketQueryRepository basketQueryRepository;
}