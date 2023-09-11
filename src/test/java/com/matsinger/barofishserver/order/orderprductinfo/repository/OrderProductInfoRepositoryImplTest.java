package com.matsinger.barofishserver.order.orderprductinfo.repository;

import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.settlement.application.SettlementQueryService;
import com.matsinger.barofishserver.settlement.dto.SettlementOrderDto;
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

    @Test
    void settlementExcepTest() {
        // given
        List<SettlementOrderDto> orderSettlementResponse = settlementQueryService.createOrderSettlementResponse(10000);
        System.out.println(orderSettlementResponse);
        // when

        // then
    }
}