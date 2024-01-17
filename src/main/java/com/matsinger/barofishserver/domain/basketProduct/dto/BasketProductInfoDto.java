package com.matsinger.barofishserver.domain.basketProduct.dto;

import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.domain.product.domain.ProductState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class BasketProductInfoDto {
    Integer id;
    Integer productId;
    ProductState state;
    String image;
    String title;
    Boolean isNeedTaxation;
    Integer discountPrice;
    Integer originPrice;
    Integer storeId;
    Integer minOrderPrice;
    Integer minStorePrice;
    ProductDeliverFeeType deliverFeeType;

    public void convertImageUrlsToFirstUrl() {
        if (this.image != null) {
            String processedUrls = this.image.substring(1, this.image.length() - 1);
            String[] parsedUrls = processedUrls.split(", ");

            this.image = parsedUrls[0];
        }
    }
}
