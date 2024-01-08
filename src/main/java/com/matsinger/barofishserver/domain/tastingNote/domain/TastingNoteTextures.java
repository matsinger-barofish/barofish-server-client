package com.matsinger.barofishserver.domain.tastingNote.domain;


import lombok.*;

import java.util.ArrayList;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class TastingNoteTextures {

    private final ArrayList<TastingNoteTexture> textures = new ArrayList<>();

    public void add(TastingNoteTexture texture) {
        this.textures.add(texture);
    }

    public void sortByScore() {
        Collections.sort(textures);
    }

    public TastingNoteTexture getTextureInTheOrderOfTheHighestScore(int idx) {
        return textures.get(idx - 1);
    }
}
