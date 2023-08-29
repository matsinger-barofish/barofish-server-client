package com.matsinger.barofishserver.compare.dto;

import com.matsinger.barofishserver.compare.filter.dto.CompareFilterDto;
import com.matsinger.barofishserver.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.product.productfilter.dto.ProductFilterValueDto;
import com.matsinger.barofishserver.store.domain.StoreDeliverFeeType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompareProductDto {
    Integer id;
    String image;
    String storeName;
    String title;
    Integer originPrice;
    Integer discountPrice;
    Integer deliveryFee;
    ProductDeliverFeeType deliverFeeType;
    Integer minOrderPrice;
    List<CompareFilterDto> compareFilters;
    List<ProductFilterValueDto> filterValues;
    String type;
    String location;
    String process;
    String usage;
    String storage;
}
