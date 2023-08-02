package com.matsinger.barofishserver.product.dto;

import com.matsinger.barofishserver.utils.Common;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductUpdateReq {

    private Integer storeId;
    private Integer categoryId;
    private String title;
    private Boolean isActive;

    private String deliveryInfo;
    private Integer deliveryFee;
    private Integer expectedDeliverDay;
    private Integer deliverBoxPerAmount;
    private String descriptionContent;
    private Boolean needTaxation;
    private Float pointRate;
    private List<Integer> difficultDeliverAddressIds;
    private List<Integer> searchFilterFieldIds;
    private List<ProductFilterValueReq> filterValues;
    private List<Common.CudInput<OptionUpdateReq, Integer>> options;
}
