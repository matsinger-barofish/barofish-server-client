package com.matsinger.barofishserver.domain.tastingNote.dto;

import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTaste;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTastes;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTexture;
import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNoteTextures;
import lombok.Getter;

import java.util.List;


@Getter
public class ProductTastingNoteResponse {

    private List<TastingNoteTaste> tastes;
    private List<TastingNoteTexture> textures;

    private String difficultyLevelOfTrimming;
    private String theScentOfTheSea;
    private List<String> recommendedCookingWay;

    public ProductTastingNoteResponse(TastingNoteTastes tastes, TastingNoteTextures textures) {
        this.tastes = tastes.getTastes();
        this.textures = List.of(
                textures.getTextureInTheOrderOfTheHighestScore(1),
                textures.getTextureInTheOrderOfTheHighestScore(2),
                textures.getTextureInTheOrderOfTheHighestScore(3)
        );
    }

    public void setDifficultyLevelOfTrimming(String difficultyLevelOfTrimming) {
        this.difficultyLevelOfTrimming = difficultyLevelOfTrimming;
    }

    public void setTheScentOfTheSea(String theScentOfTheSea) {
        this.theScentOfTheSea = theScentOfTheSea;
    }

    public void setRecommendedCookingWay(List<String> recommendedCookingWay) {
        this.recommendedCookingWay = recommendedCookingWay;
    }
}
