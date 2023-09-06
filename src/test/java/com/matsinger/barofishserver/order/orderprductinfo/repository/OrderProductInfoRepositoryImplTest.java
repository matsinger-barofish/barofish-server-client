package com.matsinger.barofishserver.order.orderprductinfo.repository;

import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest
class OrderProductInfoRepositoryImplTest {

    @Autowired
    private OrderProductInfoRepository orderProductInfoRepository;

    @Test
    void querydsl_테스트() {
        // given
        int orderProductInfoId = 1;
        // when
        OrderProductInfo findOrderProductInfo = orderProductInfoRepository.findByIdQ(orderProductInfoId);
        // then
        assertThat(findOrderProductInfo.getId()).isEqualTo(1);
    }
}