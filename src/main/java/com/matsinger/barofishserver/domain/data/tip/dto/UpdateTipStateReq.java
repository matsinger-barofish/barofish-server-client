package com.matsinger.barofishserver.domain.data.tip.dto;

import com.matsinger.barofishserver.domain.data.tip.domain.TipState;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateTipStateReq {
    List<Integer> tipIds;
    TipState state;
}
