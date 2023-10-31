package com.matsinger.barofishserver.domain.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OptionAddReq {

    private Boolean isNeeded;
    private List<OptionItemAddReq> items;
}
