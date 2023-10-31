package com.matsinger.barofishserver.domain.compare.dto;

import com.matsinger.barofishserver.domain.compare.filter.dto.CompareFilterDto;
import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.domain.product.productfilter.dto.ProductFilterValueDto;
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
