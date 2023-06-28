package com.matsinger.barofishserver.product.object;

import com.matsinger.barofishserver.product.filter.ProductFilterValueDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ProductListDto {
    Integer id;
    ProductState state;
    String image;
    String title;
    Integer discountPrice;
    Integer originPrice;
    Integer reviewCount;
    Boolean isLike;
    Integer storeId;
    String storeName;
    Integer parentCategoryId;
    List<ProductFilterValueDto> filterValues;
}
