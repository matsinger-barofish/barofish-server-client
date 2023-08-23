package com.matsinger.barofishserver.store.dto;

import com.matsinger.barofishserver.product.dto.ProductListDto;
import com.matsinger.barofishserver.review.dto.ReviewDto;
import com.matsinger.barofishserver.review.dto.ReviewStatistic;
import com.matsinger.barofishserver.store.domain.StoreDeliverFeeType;
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
    private StoreDeliverFeeType deliverFeeType;
    private Integer deliverFee;
    private Integer refundDeliverFee;
    private Integer minOrderPrice;
    private String oneLineDescription;
    private String deliverCompany;

    private Boolean isLike;

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
