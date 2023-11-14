package com.matsinger.barofishserver.domain.tastingNote.domain;

import java.util.Arrays;
import java.util.List;

public enum TastingType {

    OILY("기름기", Arrays.asList(1, 2, 3, 4, 5)),
    TASTE2("맛2", Arrays.asList(1, 2, 3, 4, 5)),
    TASTE3("맛3", Arrays.asList(1, 2, 3, 4, 5)),
    TASTE4("맛4", Arrays.asList(1, 2, 3, 4, 5)),
    TASTE5("맛5", Arrays.asList(1, 2, 3, 4, 5));

    private String taste;
    private List<Integer> score;

    TastingType(String taste, List<Integer> score) {
        this.taste = taste;
        this.score = score;
    }
}
