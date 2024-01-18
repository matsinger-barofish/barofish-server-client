package com.matsinger.barofishserver.domain.tastingNote.domain;

import lombok.*;

import java.util.ArrayList;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class TastingNoteTastes {
    private final ArrayList<TastingNoteTaste> tastes = new ArrayList<>();

    public void add(TastingNoteTaste tastingNoteTaste) {
        this.tastes.add(tastingNoteTaste);
    }

    public void sortByScore() {
        Collections.sort(tastes);
    }

    public TastingNoteTaste getTasteInTheOrderOfTheHighestScore(int order) {
        return tastes.get(order - 1);
    }
}
