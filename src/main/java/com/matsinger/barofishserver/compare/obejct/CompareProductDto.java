package com.matsinger.barofishserver.compare.obejct;

import com.matsinger.barofishserver.compare.filter.CompareFilterDto;
import com.matsinger.barofishserver.product.filter.ProductFilterValueDto;
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
    Integer discountRate;
    Integer deliveryFee;
    List<CompareFilterDto> compareFilters;
    List<ProductFilterValueDto> filterValues;
    String type;
    String location;
    String process;
    String usage;
    String storage;
}
