package com.matsinger.barofishserver.domain.product.dto;

import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.utils.Common;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductUpdateReq {

    private Integer storeId;
    private Integer categoryId;
    private String title;
    private Boolean isActive;

    private Integer deliveryFee;
    private ProductDeliverFeeType deliverFeeType;
    private Integer minOrderPrice;
    private String deliveryInfo;
    private Integer expectedDeliverDay;
    private String forwardingTime;
    private Integer deliverBoxPerAmount;
    private String descriptionContent;
    private Boolean needTaxation;
    private Float pointRate;
    private Timestamp promotionStartAt;
    private Timestamp promotionEndAt;

    private List<Integer> difficultDeliverAddressIds;
    private List<Integer> searchFilterFieldIds;
    private List<ProductFilterValueReq> filterValues;
    private List<Common.CudInput<OptionUpdateReq, Integer>> options;
}
