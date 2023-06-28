package com.matsinger.barofishserver.searchFilter.object;

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
