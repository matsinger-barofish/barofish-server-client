package com.matsinger.barofishserver.domain.product.difficultDeliverAddress.application;

import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.domain.DifficultDeliverAddress;
import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.repository.DifficultDeliverAddressRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
class DifficultDeliverAddressQueryServiceTest {

    @Autowired private DifficultDeliverAddressRepository difficultDeliverAddressRepository;

    public DifficultDeliverAddressQueryServiceTest(DifficultDeliverAddressRepository difficultDeliverAddressRepository) {
        this.difficultDeliverAddressRepository = difficultDeliverAddressRepository;
    }

    @DisplayName("")
    @Test
    void test() {
        // given
        String string1 = "11110";
        String string2 = "11110";
        boolean equals = string1.equals(string2);
        // when

        // then
    }

    @DisplayName("")
    @Test
    void difficultTest() {
        // given
        List<Integer> productIds = List.of(11, 12);
        for (Integer productId : productIds) {
            difficultDeliverAddressRepository.findAllByProductId(productId);
            List<DifficultDeliverAddress> difficultDeliverAddresses = difficultDeliverAddressRepository.findAllByProductId(productId);

            List<String> bcodes = difficultDeliverAddresses.stream().map(v -> v.getBcode()).toList();
        }
        // when

        // then
    }
}