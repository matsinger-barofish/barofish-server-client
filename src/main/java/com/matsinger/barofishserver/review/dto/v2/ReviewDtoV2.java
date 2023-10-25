package com.matsinger.barofishserver.review.dto.v2;

import lombok.Getter;
import org.w3c.dom.stylesheets.LinkStyle;

import java.sql.Timestamp;
import java.util.List;

@Getter
public class ReviewDtoV2 {

    private Integer userId;
    private String userName;
    private String userNickname;
    private String userGrade;
    private String storeName;
    private Integer productId;
    private String productName;
    private int originPrice;
    private int discountPrice;
    private String productImage;
    private int reviewId;
    private String reviewContent;
    private Timestamp createdAt;
    private String images;
    private String[] imageUrls;
    private boolean isLike;
    private Long likeSum;

    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setProductImage(String parsedImageUrl) {
        this.productImage = parsedImageUrl;
    }
}
