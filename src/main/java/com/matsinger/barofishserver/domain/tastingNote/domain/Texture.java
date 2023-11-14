package com.matsinger.barofishserver.domain.tastingNote.domain;

import java.util.Arrays;
import java.util.List;

public enum Texture {

    TENDERNESS_SOFTNESS("탱글함_부드러움", Arrays.asList(1, 2, 3, 4, 5)),
    Texture_Texture2("식감2", Arrays.asList(1, 2, 3, 4, 5)),
    Texture_Texture3("식감3", Arrays.asList(1, 2, 3, 4, 5)),
    Texture_Texture4("식감4", Arrays.asList(1, 2, 3, 4, 5)),
    Texture_Texture5("식감5", Arrays.asList(1, 2, 3, 4, 5));

    private String texture;
    private List<Integer> score;

    Texture(String texture, List<Integer> score) {
        this.texture = texture;
        this.score = score;
    }
}
