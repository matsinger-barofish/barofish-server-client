package com.matsinger.barofishserver.order.orderprductinfo.repository;

import com.matsinger.barofishserver.domain.order.orderprductinfo.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.domain.order.orderprductinfo.repository.OrderProductInfoRepositoryImpl;
import com.matsinger.barofishserver.domain.settlement.application.SettlementQueryService;
import com.matsinger.barofishserver.domain.settlement.dto.SettlementOrderDto;
import com.matsinger.barofishserver.domain.settlement.dto.TempDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

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
        List<TempDto> tempDtos = orderProductInfoRepository.queryTest(orderProductInfoId);
        System.out.println("tempDtos = " + tempDtos);
        // then
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