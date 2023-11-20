package com.matsinger.barofishserver.domain.tastingNote.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class TastingNoteTaste implements Comparable<TastingNoteTaste> {

    private String taste;
    private Double score;


    // 내림차순 정렬
    @Override
    public int compareTo(@NotNull TastingNoteTaste o) {
        return (int) (o.score - this.score);
    }
}
