package com.matsinger.barofishserver.review.object;

import com.matsinger.barofishserver.order.object.OrderDto;
import com.matsinger.barofishserver.product.object.ProductListDto;
import com.matsinger.barofishserver.store.object.SimpleStore;
import com.matsinger.barofishserver.user.object.UserInfo;
import com.matsinger.barofishserver.user.object.UserInfoDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
public class ReviewDto {
    Integer id;
    ProductListDto simpleProduct;
    SimpleStore store;
    UserInfoDto user;
    List<ReviewEvaluationType> evaluations;
    String[] images;
    String content;
    Boolean isLike;
    Integer likeCount;
    Timestamp createdAt;
}
