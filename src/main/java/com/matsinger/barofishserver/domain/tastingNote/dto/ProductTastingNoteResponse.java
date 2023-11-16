package com.matsinger.barofishserver.domain.tastingNote.dto;

import com.matsinger.barofishserver.domain.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ProductTastingNoteResponse {

    private Integer productId;

    private Double taste1Sum;
    private Double taste2Sum;
    private Double taste3Sum;
    private Double taste4Sum;
    private Double taste5Sum;
    private Double texture1Sum;
    private Double texture2Sum;
    private Double texture3Sum;
    private Double texture4Sum;
    private Double texture5Sum;

    private String recommendedCookingWay;
    private String theScentOfTheSea;
    private String difficultyLevelOfTrimming;

    public void roundScoresToSecondDecimalPlace() {
        this.taste1Sum = Math.round(taste1Sum * 10) / 10.0;
        this.taste2Sum = Math.round(taste2Sum * 10) / 10.0;
        this.taste3Sum = Math.round(taste3Sum * 10) / 10.0;
        this.taste4Sum = Math.round(taste4Sum * 10) / 10.0;
        this.taste5Sum = Math.round(taste5Sum * 10) / 10.0;
        this.texture1Sum = Math.round(texture1Sum * 10) / 10.0;
        this.texture2Sum = Math.round(texture2Sum * 10) / 10.0;
        this.texture3Sum = Math.round(texture3Sum * 10) / 10.0;
        this.texture4Sum = Math.round(texture4Sum * 10) / 10.0;
        this.texture5Sum = Math.round(texture5Sum * 10) / 10.0;
    }

    public void setTastingNoteAdditionalInfo(Product product) {
        try {
            this.recommendedCookingWay = product.getRecommendedCookingWay();
            this.theScentOfTheSea = product.getTheScentOfTheSea();
            this.difficultyLevelOfTrimming = product.getDifficultyLevelOfTrimming();
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("상품의 테이스팅노트 정보가 없습니다.");
        }
    }
}
