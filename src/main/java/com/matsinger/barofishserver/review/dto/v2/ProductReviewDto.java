package com.matsinger.barofishserver.review.dto.v2;

import com.matsinger.barofishserver.review.dto.ReviewDto;

import java.util.List;

public class ProductReviewDto {

    private Integer productId;
    private ProductDtoInReview productDto;
    private Integer reviewCount;
    private Integer tasteSum;
    private Integer freshSum;
    private Integer priceSum;
    private Integer packingSum;
    private Integer sizeSum;
    private List<ReviewDtoV2> reviewDtos;
    private StoreDtoInReview storeDto;



}
