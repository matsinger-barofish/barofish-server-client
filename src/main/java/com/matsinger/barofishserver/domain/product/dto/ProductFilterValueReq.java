package com.matsinger.barofishserver.domain.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductFilterValueReq {

    private Integer compareFilterId;
    private String value;
}
