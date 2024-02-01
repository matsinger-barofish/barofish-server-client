package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class BasketTastingNoteDeleteReq {

    private List<Integer> productId;
}
