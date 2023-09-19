package com.matsinger.barofishserver.settlement.dto;

import lombok.Getter;

@Getter
public class TempDto {

    private Long reviewLike;
    private Integer reviewId;

    @Override
    public String toString() {
        return "TempDto{" +
                "reviewLike=" + reviewLike +
                ", reviewId=" + reviewId +
                '}';
    }
}