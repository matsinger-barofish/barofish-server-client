package com.matsinger.barofishserver.domain.tastingNote.domain;

public enum Texture {

    TENDERNESS_SOFTNESS("탱글함_부드러움"),
    texture2("식감2"),
    texture3("식감3"),
    texture4("식감4"),
    texture5("식감5");

    private String texture;

    Texture(String texture) {
        this.texture = texture;
    }

    public String findBy(String taste) {
        for (Texture texture : Texture.values()) {
            String stringTexture = texture.toString().toLowerCase();
            if (stringTexture.equals(taste)) {
                return stringTexture;
            }
        }
        throw new IllegalArgumentException("상품의 식감 정보를 찾을 수 없습니다.");
    }
}
