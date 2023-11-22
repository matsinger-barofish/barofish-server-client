package com.matsinger.barofishserver.domain.tastingNote.dto;

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

    private Integer productId;

    private String taste1;
    private Double taste1Score;
    private String taste2;
    private Double taste2Score;
    private String taste3;
    private Double taste3Score;
    private String taste4;
    private Double taste4Score;
    private String taste5;
    private Double taste5Score;

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

    private String difficultyLevelOfTrimming;
    private String theScentOfTheSea;
    private String recommendedCookingWay;

    public void roundScoresToSecondDecimalPlace() {
        this.taste1Score = Math.round(taste1Score * 10) / 10.0;
        this.taste2Score = Math.round(taste2Score * 10) / 10.0;
        this.taste3Score = Math.round(taste3Score * 10) / 10.0;
        this.taste4Score = Math.round(taste4Score * 10) / 10.0;
        this.taste5Score = Math.round(taste5Score * 10) / 10.0;
        this.texture1Score = Math.round(texture1Score * 10) / 10.0;
        this.texture2Score = Math.round(texture2Score * 10) / 10.0;
        this.texture3Score = Math.round(texture3Score * 10) / 10.0;
        this.texture4Score = Math.round(texture4Score * 10) / 10.0;
        this.texture5Score = Math.round(texture5Score * 10) / 10.0;
    }

    public TastingNoteTastes getTastes() {
        TastingNoteTastes tastingNoteTastes = new TastingNoteTastes();
        tastingNoteTastes.add(TastingNoteTaste.builder().taste(this.taste1).score(this.taste1Score).build());
        tastingNoteTastes.add(TastingNoteTaste.builder().taste(this.taste2).score(this.taste2Score).build());
        tastingNoteTastes.add(TastingNoteTaste.builder().taste(this.taste3).score(this.taste3Score).build());
        tastingNoteTastes.add(TastingNoteTaste.builder().taste(this.taste4).score(this.taste4Score).build());
        tastingNoteTastes.add(TastingNoteTaste.builder().taste(this.taste5).score(this.taste5Score).build());
        return tastingNoteTastes;
    }

    public TastingNoteTextures getTextures() {
        TastingNoteTextures tastingNoteTextures = new TastingNoteTextures();
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture1).score(this.texture1Score).build());
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture2).score(this.texture2Score).build());
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture3).score(this.texture3Score).build());
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture4).score(this.texture4Score).build());
        tastingNoteTextures.add(TastingNoteTexture.builder().texture(this.texture5).score(this.texture5Score).build());
        return tastingNoteTextures;
    }
}
