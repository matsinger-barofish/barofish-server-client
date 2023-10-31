package com.matsinger.barofishserver.domain.searchFilter.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchFilterFieldDto {
    private int id;
    private int searchFilterId;
    private String field;
}
