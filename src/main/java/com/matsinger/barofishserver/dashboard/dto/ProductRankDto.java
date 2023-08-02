package com.matsinger.barofishserver.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRankDto {
    Integer productId;
    String productName;
    String storeName;
    Integer count;
    Integer rank;
}
