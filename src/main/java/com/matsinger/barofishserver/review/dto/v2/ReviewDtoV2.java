package com.matsinger.barofishserver.review.dto.v2;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ReviewDtoV2 {

    private Integer userId;
    private String userName;
    private String userNickname;
    private String userGrade;
    private Integer productId;
    private String productName;
    private int originPrice;
    private int discountPrice;
    private String productImage;
    private String reviewContent;
    private Timestamp createdAt;
    private String images;
    private String[] imageUrls;
    private Long likeSum;

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setProductImage(String parsedImageUrl) {
        this.productImage = parsedImageUrl;
    }
}
