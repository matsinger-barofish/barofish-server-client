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

    private Integer taste1;
    private Integer taste2;
    private Integer taste3;
    private Integer taste4;
    private Integer taste5;

    private Integer texture1;
    private Integer texture2;
    private Integer texture3;
    private Integer texture4;
    private Integer texture5;
    private Integer texture6;
    private Integer texture7;
    private Integer texture8;

    public TastingNote toEntity() {
        return TastingNote.builder()
                .orderProductInfoId(this.orderProductInfoId)
                .taste1(this.taste1)
                .taste2(this.taste2)
                .taste3(this.taste3)
                .taste4(this.taste4)
                .taste5(this.taste5)
                .tendernessSoftness(this.texture1)
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
