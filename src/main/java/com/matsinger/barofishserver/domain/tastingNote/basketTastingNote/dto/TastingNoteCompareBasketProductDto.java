package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.dto;

import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.domain.product.domain.ProductState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class TastingNoteCompareBasketProductDto {

    private Integer id;
    private Integer productId;
    private ProductState state;
    private String image;
    private String title;
    private Boolean isNeedTaxation;
    private Integer discountPrice;
    private Integer originPrice;
    private Long reviewCount;
    private Boolean isLike;
    private Integer storeId;
    private String storeName;
    private Integer minOrderPrice;
    private String storeImage;
    private ProductDeliverFeeType deliverFeeType;
    private Integer parentCategoryId;
    private boolean isTastingNoteExists;

    public void setImage(String image) {
        this.image = image;
    }

    public void isTastingNoteExists(boolean isTastingNoteExists) {
        this.isTastingNoteExists = isTastingNoteExists;
    }
}
