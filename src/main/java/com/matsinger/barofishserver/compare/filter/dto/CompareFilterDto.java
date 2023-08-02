package com.matsinger.barofishserver.compare.filter.dto;

import jakarta.persistence.Column;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompareFilterDto {
    private int id;
    private String name;
}
