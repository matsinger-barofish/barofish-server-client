package com.matsinger.barofishserver.domain.order.dto;

import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.store.domain.StoreInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class OrderStoreMapper {

    private StoreInfo storeInfo;
    private List<OrderProductInfo> orderProductInfos = new ArrayList<>();
}
