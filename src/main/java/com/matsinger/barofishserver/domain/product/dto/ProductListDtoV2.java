package com.matsinger.barofishserver.domain.product.dto;

import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.domain.product.domain.ProductState;
import com.matsinger.barofishserver.domain.product.productfilter.dto.ProductFilterValueDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductListDtoV2 {
    Integer id;
    ProductState state;
    String image;
    String title;
    Boolean isNeedTaxation;
    Integer discountPrice;
    Integer originPrice;
    Long reviewCount;
    Boolean isLike;
    Integer storeId;
    String storeName;
    Integer minOrderPrice;
    String storeImage;
    ProductDeliverFeeType deliverFeeType;
    Integer parentCategoryId;
    List<ProductFilterValueDto> filterValues;
}
