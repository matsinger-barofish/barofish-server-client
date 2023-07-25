package com.matsinger.barofishserver.product.dto;

import com.matsinger.barofishserver.product.domain.ProductState;
import com.matsinger.barofishserver.product.productfilter.dto.ProductFilterValueDto;
import lombok.*;

import javax.annotation.security.DenyAll;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductListDto {
    Integer id;
    ProductState state;
    String image;
    String title;
    Boolean isNeedTaxation;
    Integer discountPrice;
    Integer originPrice;
    Integer reviewCount;
    Boolean isLike;
    Integer storeId;
    String storeName;
    Integer parentCategoryId;
    List<ProductFilterValueDto> filterValues;
}
