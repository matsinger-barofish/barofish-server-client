package com.matsinger.barofishserver.domain.searchFilter.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SearchFilterDto {
    private int id;
    private String name;
    private List<SearchFilterFieldDto> searchFilterFields;
}
