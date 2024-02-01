package com.matsinger.barofishserver.domain.order.orderprductinfo.repository;

import com.matsinger.barofishserver.domain.settlement.dto.SettlementOrderRawDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductInfoRepositoryCustom {

    List<SettlementOrderRawDto> getExcelRawDataWithNotSettled(Integer storeId);
}
