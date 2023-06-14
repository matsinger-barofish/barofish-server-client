package com.matsinger.barofishserver.product.object;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductListDto {
    Integer id;
    String image;
    String title;
    Integer discountRate;
    Integer originPrice;
    Integer reviewCount;
    Boolean isLike;
}
