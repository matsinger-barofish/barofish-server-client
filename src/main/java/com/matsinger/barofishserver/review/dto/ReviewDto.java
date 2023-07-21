package com.matsinger.barofishserver.review.dto;

import com.matsinger.barofishserver.product.dto.ProductListDto;
import com.matsinger.barofishserver.review.domain.ReviewEvaluationType;
import com.matsinger.barofishserver.store.dto.SimpleStore;
import com.matsinger.barofishserver.userinfo.dto.UserInfoDto;
import lombok.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
