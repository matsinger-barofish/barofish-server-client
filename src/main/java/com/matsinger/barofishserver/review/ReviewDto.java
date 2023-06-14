package com.matsinger.barofishserver.review;

import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.object.ProductListDto;
import com.matsinger.barofishserver.product.object.SimpleProductDto;
import com.matsinger.barofishserver.store.object.SimpleStore;
import com.matsinger.barofishserver.user.object.UserInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class ReviewDto {
    Integer id;
    ProductListDto simpleProduct;
    SimpleStore store;
    UserInfo user;
    ReviewEvaluation evaluation;
    String[] images;
    String content;
    Timestamp createdAt;
}
