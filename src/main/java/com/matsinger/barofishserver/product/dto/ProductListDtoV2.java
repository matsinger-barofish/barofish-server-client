package com.matsinger.barofishserver.product.dto;

import com.matsinger.barofishserver.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.product.domain.ProductState;
import com.matsinger.barofishserver.product.productfilter.dto.ProductFilterValueDto;
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
