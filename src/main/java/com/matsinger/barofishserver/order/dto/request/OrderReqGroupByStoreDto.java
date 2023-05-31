package com.matsinger.barofishserver.order.dto.request;

import java.util.List;

public class OrderReqGroupByStoreDto {

    private int userId;
    private int storeId;
    private List<OrderReqProductInfoDto> products;
}
