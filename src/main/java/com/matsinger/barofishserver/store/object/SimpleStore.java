package com.matsinger.barofishserver.store.object;

import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.object.ProductListDto;
import com.matsinger.barofishserver.review.Review;
import com.matsinger.barofishserver.review.ReviewDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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

    Boolean isLike;

    List<ProductListDto> products = new ArrayList<>();
    List<ReviewDto> reviews = new ArrayList<>();
    Integer reviewCount;
    Integer productCount;

    List<ReviewDto> imageReviews = new ArrayList<>();
}
