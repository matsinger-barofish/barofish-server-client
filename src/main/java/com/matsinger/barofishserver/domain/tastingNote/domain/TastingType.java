package com.matsinger.barofishserver.domain.tastingNote.domain;

public enum TastingType {

    OILY("기름기"),
    TASTE2("맛2"),
    TASTE3("맛3"),
    TASTE4("맛4"),
    TASTE5("맛5");

    private String taste;

    TastingType(String taste) {
        this.taste = taste;
    }

    public String findBy(String taste) {
        for (TastingType type : TastingType.values()) {
            String stringType = type.toString().toLowerCase();
            if (stringType.equals(taste)) {
                return stringType;
            }
        }
        throw new IllegalArgumentException("상품의 맛 정보를 찾을 수 없습니다.");
    }
}
