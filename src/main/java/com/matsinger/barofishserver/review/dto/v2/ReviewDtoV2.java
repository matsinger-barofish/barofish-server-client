package com.matsinger.barofishserver.review.dto.v2;

import com.matsinger.barofishserver.grade.domain.Grade;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
public class ReviewDtoV2 {

    private Integer userId;
    private String userName;
    private String userGrade;
    private String productName;
    private String reviewContent;
    private Timestamp createdAt;
    private String images;
    private String[] imageUrls;
    private Long likeSum;

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public String toString() {
        return "ReviewDtoV2{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userGrade='" + userGrade + '\'' +
                ", productName='" + productName + '\'' +
                ", reviewContent='" + reviewContent + '\'' +
                ", createdAt=" + createdAt +
                ", images=" + images +
                ", likeSum=" + likeSum +
                '}';
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }
}
