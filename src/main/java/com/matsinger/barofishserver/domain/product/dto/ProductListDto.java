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
public class ProductListDto {
    Integer id;
    Integer productId;
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
    ProductDeliverFeeType deliverFeeType;
    Integer minOrderPrice;
    Integer productDeliveryFee;
    Boolean isConditional;
    Integer minStorePrice;
    Integer storeDeliverFee;
    String storeImage;
    Integer parentCategoryId;
    List<ProductFilterValueDto> filterValues;

    Boolean tastingNoteExists;

    public void convertImageUrlsToFirstUrl() {
        String processedUrls = this.image.substring(1, this.image.length() - 1);
        String[] parsedUrls = processedUrls.split(", ");

        this.image = parsedUrls[0];
    }
}
