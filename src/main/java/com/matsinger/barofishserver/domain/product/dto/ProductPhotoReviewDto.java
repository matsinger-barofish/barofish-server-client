package com.matsinger.barofishserver.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class ProductPhotoReviewDto {

    private Integer reviewId;
    private Integer imageCount;
    private List<String> imageUrls;
}
