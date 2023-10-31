package com.matsinger.barofishserver.domain.review.dto.v2;

import com.matsinger.barofishserver.domain.product.domain.ProductState;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ReviewDtoV2 {

    private Integer userId;
    private String userName;
    private String userNickname;
    private String userGrade;
    private String storeName;
    private Integer productId;
    private ProductState productState;
    private String productName;
    private int originPrice;
    private int discountPrice;
    private String productImage;
    private int reviewId;
    private String reviewContent;
    private Timestamp createdAt;
    private String images;
    private String[] imageUrls;
    private boolean sameUserLike;
    private Long likeSum;

    public void setSameUserLike(boolean isLike) {
        this.sameUserLike = isLike;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setProductImage(String parsedImageUrl) {
        this.productImage = parsedImageUrl;
    }
}
