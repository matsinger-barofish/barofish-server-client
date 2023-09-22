package com.matsinger.barofishserver.order.orderprductinfo.repository;

import com.matsinger.barofishserver.settlement.dto.SettlementOrderRawDto;
import com.matsinger.barofishserver.settlement.dto.TempDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductInfoRepositoryCustom {

    List<TempDto> queryTest(int orderProductInfoId);

    List<SettlementOrderRawDto> getExcelRawDataWithNotSettled(Integer storeId);
}
