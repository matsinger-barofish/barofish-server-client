package com.matsinger.barofishserver.store.object;

import com.matsinger.barofishserver.product.object.ProductListDto;
import com.matsinger.barofishserver.review.object.ReviewDto;
import com.matsinger.barofishserver.review.object.ReviewStatistic;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class SimpleStore {
    Integer storeId;
    String backgroundImage;
    String profileImage;
    String name;
    String location;
    String[] keyword;
    String visitNote;
    StoreDeliverFeeType deliverFeeType;
    Integer deliverFee;
    Integer minOrderPrice;
    String oneLineDescription;

    Boolean isLike;

    List<ReviewStatistic> reviewStatistic;

    List<ProductListDto> products;
    List<ReviewDto> reviews;
    Integer reviewCount;
    Integer productCount;

    List<ReviewDto> imageReviews;
}
