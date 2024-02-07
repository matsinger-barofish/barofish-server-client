package com.matsinger.barofishserver.domain.search.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SearchDirectResponse {

    private final List<Integer> productIds;
    private final List<SearchProductDto> searchProductDtos;
}
