package com.matsinger.barofishserver.domain.store.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class StoreInfoQueryServiceTest {

    @Autowired private StoreInfoQueryService storeInfoQueryService;
}