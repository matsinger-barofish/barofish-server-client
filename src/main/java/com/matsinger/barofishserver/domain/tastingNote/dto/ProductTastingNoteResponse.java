package com.matsinger.barofishserver.domain.tastingNote.dto;

import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTaste;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTastes;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTexture;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTextures;
import lombok.Getter;

import java.util.List;


@Getter
public class ProductTastingNoteResponse {

    private Integer id;
    private Integer productId;
    private String image;
    private String productTitle;
    private String storeName;
    private Integer originPrice;
    private Integer discountPrice;
    private Integer deliveryFee;
    private ProductDeliverFeeType deliverFeeType;
    private Integer minOrderPrice;

    private List<TastingNoteTaste> tastes;
    private List<TastingNoteTexture> textures;

    private Double difficultyLevelOfTrimming;
    private Double theScentOfTheSea;
    private List<String> recommendedCookingWay;

    public ProductTastingNoteResponse(TastingNoteTastes tastes, TastingNoteTextures textures) {
        this.tastes = tastes.getTastes();
        this.textures = List.of(
                textures.getTextureInTheOrderOfTheHighestScore(1),
                textures.getTextureInTheOrderOfTheHighestScore(2),
                textures.getTextureInTheOrderOfTheHighestScore(3)
        );
    }

    public void setDifficultyLevelOfTrimming(Double difficultyLevelOfTrimming) {
        this.difficultyLevelOfTrimming = difficultyLevelOfTrimming;
    }

    public void setTheScentOfTheSea(Double theScentOfTheSea) {
        this.theScentOfTheSea = theScentOfTheSea;
    }

    public void setRecommendedCookingWay(List<String> recommendedCookingWay) {
        this.recommendedCookingWay = recommendedCookingWay;
    }

    public void setProductInfo(ProductTastingNoteInquiryDto dto) {
        this.id = dto.getId();
        this.productId = dto.getProductId();
        this.storeName = dto.getStoreName();
        this.productTitle = dto.getProductTitle();
        this.originPrice = dto.getOriginPrice();
        this.discountPrice = dto.getDiscountPrice();
        this.deliveryFee = dto.getDeliveryFee();
        this.deliverFeeType = dto.getDeliverFeeType();
        this.minOrderPrice = dto.getOriginPrice();
    }

    public void setImage(String image) {
        this.image = image;
    }
}
