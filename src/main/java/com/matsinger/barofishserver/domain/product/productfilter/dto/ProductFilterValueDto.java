package com.matsinger.barofishserver.domain.product.productfilter.dto;

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
