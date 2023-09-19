package com.matsinger.barofishserver.order.orderprductinfo.repository;

import com.matsinger.barofishserver.review.domain.Review;
import com.matsinger.barofishserver.settlement.dto.SettlementOrderRawDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductInfoRepositoryCustom {

    List<Review> queryTest(int orderProductInfoId);

    List<SettlementOrderRawDto> getExcelRawDataWithNotSettled(Integer storeId);
}
