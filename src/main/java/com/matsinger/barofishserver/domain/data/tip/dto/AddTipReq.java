package com.matsinger.barofishserver.domain.data.tip.dto;

import com.matsinger.barofishserver.domain.data.tip.domain.TipType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddTipReq {
    String title;
    String description;
    TipType type;
    String content;
}
