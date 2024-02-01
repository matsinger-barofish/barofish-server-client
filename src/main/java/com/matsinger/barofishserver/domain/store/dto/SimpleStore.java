package com.matsinger.barofishserver.domain.store.dto;

import com.matsinger.barofishserver.domain.review.dto.ReviewDto;
import com.matsinger.barofishserver.domain.review.dto.ReviewStatistic;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleStore {
    private Integer storeId;
    private String loginId;
    private String backgroundImage;
    private Boolean isReliable;
    private String profileImage;
    private String name;
    private String location;
    private String[] keyword;
    private String visitNote;
    private Integer refundDeliverFee;
    private String oneLineDescription;
    private String deliverCompany;

    private Boolean isLike;

    private Boolean isConditional;
    private Integer minStorePrice;
    private Integer deliveryFee;

    @Builder.Default
    private List<ReviewStatistic> reviewStatistic = new ArrayList<>();

    @Builder.Default
    private List<ProductListDto> products = new ArrayList<>();

    @Builder.Default
    private List<ReviewDto> reviews = new ArrayList<>();
    private Integer reviewCount;
    private Integer productCount;

    @Builder.Default
    private List<ReviewDto> imageReviews = new ArrayList<>();
}
