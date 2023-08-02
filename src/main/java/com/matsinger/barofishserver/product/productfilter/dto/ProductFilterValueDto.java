package com.matsinger.barofishserver.product.productfilter.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterValueDto {
    Integer compareFilterId;
    String compareFilterName;
    String value;
}
