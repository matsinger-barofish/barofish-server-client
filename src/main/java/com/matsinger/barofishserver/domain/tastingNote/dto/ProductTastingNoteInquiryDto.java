package com.matsinger.barofishserver.domain.tastingNote.dto;

import com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTaste;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTastes;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTexture;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTextures;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class ProductTastingNoteInquiryDto {

    private Integer id;
    private Integer productId;
    private String images;
    private String storeName;
    private String productTitle;
    private Integer originPrice;
    private Integer discountPrice;
    private Integer deliveryFee;
    private ProductDeliverFeeType deliverFeeType;
    private Integer minOrderPrice;

    private String oily;
    private Double oilyScore;
    private String sweet;
    private Double sweetScore;
    private String lightTaste;
    private Double lightTasteScore;
    private String umami;
    private Double umamiScore;
    private String salty;
    private Double saltyScore;

    private String texture1;
    private Double texture1Score;
    private String texture2;
    private Double texture2Score;
    private String texture3;
    private Double texture3Score;
    private String texture4;
    private Double texture4Score;
    private String texture5;
    private Double texture5Score;
    private String texture6;
    private Double texture6Score;
    private String texture7;
    private Double texture7Score;
    private String texture8;
    private Double texture8Score;

    private String difficultyLevelOfTrimming;
    private String theScentOfTheSea;
    private String recommendedCookingWay;

    public void roundScoresToSecondDecimalPlace() {
        this.oilyScore = Math.round(oilyScore * 10) / 10.0;
        this.sweetScore = Math.round(sweetScore * 10) / 10.0;
        this.lightTasteScore = Math.round(lightTasteScore * 10) / 10.0;
        this.umamiScore = Math.round(umamiScore * 10) / 10.0;
        this.saltyScore = Math.round(saltyScore * 10) / 10.0;
        this.texture1Score = texture1Score != null ? Math.round(texture1Score * 10) / 10.0 : 0;
        this.texture2Score = texture2Score != null ? Math.round(texture2Score * 10) / 10.0 : 0;
        this.texture3Score = texture3Score != null ? Math.round(texture3Score * 10) / 10.0 : 0;
        this.texture4Score = texture4Score != null ? Math.round(texture4Score * 10) / 10.0 : 0;
        this.texture5Score = texture5Score != null ? Math.round(texture5Score * 10) / 10.0 : 0;
        this.texture6Score = texture6Score != null ? Math.round(texture6Score * 10) / 10.0 : 0;
        this.texture7Score = texture7Score != null ? Math.round(texture7Score * 10) / 10.0 : 0;
        this.texture8Score = texture8Score != null ? Math.round(texture8Score * 10) / 10.0 : 0;
    }

    public TastingNoteTastes getTastes() {
        TastingNoteTastes tastingNoteTastes = new TastingNoteTastes();
        tastingNoteTastes.add(TastingNoteTaste.builder().taste(this.oily).score(this.oilyScore).build());
        tastingNoteTastes.add(TastingNoteTaste.builder().taste(this.sweet).score(this.sweetScore).build());
        tastingNoteTastes.add(TastingNoteTaste.builder().taste(this.lightTaste).score(this.lightTasteScore).build());
        tastingNoteTastes.add(TastingNoteTaste.builder().taste(this.umami).score(this.umamiScore).build());
        tastingNoteTastes.add(TastingNoteTaste.builder().taste(this.salty).score(this.saltyScore).build());
        return tastingNoteTastes;
    }

    public TastingNoteTextures getTextures() {
        TastingNoteTextures tastingNoteTextures = new TastingNoteTextures();
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture1).score(this.texture1Score).build());
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture2).score(this.texture2Score).build());
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture3).score(this.texture3Score).build());
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture4).score(this.texture4Score).build());
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture5).score(this.texture5Score).build());
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture6).score(this.texture6Score).build());
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture7).score(this.texture7Score).build());
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture8).score(this.texture8Score).build());
        return tastingNoteTextures;
    }
}
