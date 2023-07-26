package com.matsinger.barofishserver.data.curation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CurationDeleteProductReq {
    Integer curationId;
    List<Integer> productIds;
}
