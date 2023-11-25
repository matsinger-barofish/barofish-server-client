package com.matsinger.barofishserver.domain.tastingNote.dto;

import com.matsinger.barofishserver.domain.tastingNote.domain.TastingNote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class TastingNoteCreateRequest {

    private Integer orderProductInfoId;

    private Double taste1;
    private Double taste2;
    private Double taste3;
    private Double taste4;
    private Double taste5;

    private Double texture1;
    private Double texture2;
    private Double texture3;
    private Double texture4;
    private Double texture5;
    private Double texture6;
    private Double texture7;
    private Double texture8;

    public TastingNote toEntity() {
        return TastingNote.builder()
                .orderProductInfoId(this.orderProductInfoId)
                .taste1(this.taste1)
                .taste2(this.taste2)
                .taste3(this.taste3)
                .taste4(this.taste4)
                .taste5(this.taste5)
                .texture1(this.texture1)
                .texture2(this.texture2)
                .texture3(this.texture3)
                .texture4(this.texture4)
                .texture5(this.texture5)
                .texture5(this.texture6)
                .texture5(this.texture7)
                .texture5(this.texture8)
                .build();
    }
}
