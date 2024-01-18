package com.matsinger.barofishserver.domain.product.dto;

import com.matsinger.barofishserver.utils.Common;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OptionUpdateReq {

    private Boolean isNeeded;
    private List<Common.CudInput<OptionItemUpdateReq, Integer>> items;
}
