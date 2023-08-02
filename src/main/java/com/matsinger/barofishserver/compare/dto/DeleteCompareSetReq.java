package com.matsinger.barofishserver.compare.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DeleteCompareSetReq {
    private List<Integer> compareSetIds;
}
