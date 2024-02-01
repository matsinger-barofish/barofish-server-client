package com.matsinger.barofishserver.domain.search.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchProductDto {
    Integer id;
    String title;
}
