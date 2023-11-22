package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.dto;

import lombok.Getter;

@Getter
public class BasketTastingNoteAddDeleteReq {

    private Integer productId;

    @Override
    public String toString() {
        return "BasketTastingNoteAddDeleteReq{" +
                "productId=" + productId +
                '}';
    }
}
