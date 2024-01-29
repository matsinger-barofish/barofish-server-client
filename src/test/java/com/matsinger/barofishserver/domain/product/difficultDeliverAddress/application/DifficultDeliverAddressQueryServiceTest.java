package com.matsinger.barofishserver.domain.product.difficultDeliverAddress.application;

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
        List<Integer> productIds = List.of(5, 6);
        for (Integer productId : productIds) {

            List<String> difficultDeliveryBcodes = difficultDeliverAddressRepository.findAllByProductId(productId)
                    .stream().map(v -> v.getBcode()).toList();

            String orderDeliverPlaceBcode = "11110";

            boolean cannotDeliver = false;
            for (String difficultDeliveryBcode : difficultDeliveryBcodes) {
                cannotDeliver = difficultDeliveryBcode.length() >= 5 &&
                        difficultDeliveryBcode.substring(0, 5).equals(orderDeliverPlaceBcode);
            }
        }
        // when

        // then
    }
}