package com.matsinger.barofishserver.order.orderprductinfo.repository;

import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.settlement.application.SettlementQueryService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest
class OrderProductInfoRepositoryImplTest {

    @Autowired
    private OrderProductInfoRepository orderProductInfoRepository;
    @Autowired
    private OrderProductInfoRepositoryImpl orderProductInfoRepositoryImpl;
    @Autowired
    private SettlementQueryService settlementQueryService;

    @Test
    void querydsl_테스트() {
        // given
        int orderProductInfoId = 1;
        // when
        OrderProductInfo findOrderProductInfo = orderProductInfoRepository.findByIdQ(orderProductInfoId);
        // then
        assertThat(findOrderProductInfo.getId()).isEqualTo(1);
    }

    @DisplayName("OrderProductInfo에 다른 데이블을 조인해서 가져오는 경우 타입을 어떻게 받아야 하는지 테스트")
    @Test
    void joinFormTest() {
        // given
        settlementQueryService.getSettlementExcel();
        // when

        // then
    }
}