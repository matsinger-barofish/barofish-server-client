package com.matsinger.barofishserver.order.orderprductinfo.repository;

import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.settlement.dto.SettlementExcelDownloadRawDto;
import com.matsinger.barofishserver.settlement.dto.SettlementOrderDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductInfoRepositoryCustom {

    OrderProductInfo findByIdQ(int orderProductInfoId);

    List<SettlementExcelDownloadRawDto> getExcelRawDataWithNotSettled1();

    List<SettlementOrderDto> getExcelRawDataWithNotSettled2();

    List<SettlementOrderDto> getExcelRawDataWithNotSettled3();
}
