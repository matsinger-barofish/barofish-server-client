package com.matsinger.barofishserver.domain.product.domain;

import java.util.Arrays;
import java.util.List;

public enum TastingNoteAdditionalInfo {

    DIFFICULTY_LEVEL_OF_TRIMMING("손질난이도", Arrays.asList("상", "중", "하")),
    THE_SCENT_OF_THE_SEA("바다향", Arrays.asList("초", "중", "고")),
    RECOMMENDED_COOKING_WAY("조리방법추천", Arrays.asList("찜", "회", "구이", "탕", "조림"));

    private String additionalInfo;
    private List<String> infoDescription;

    TastingNoteAdditionalInfo(String additionalInfo, List<String> infoDescription) {
        this.additionalInfo = additionalInfo;
        this.infoDescription = infoDescription;
    }
}
