package com.matsinger.barofishserver.domain.data.curation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SortCurationReq {
    List<Integer> curationIds;
}
