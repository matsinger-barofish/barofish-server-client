package com.matsinger.barofishserver.review.dto.v2;

import com.matsinger.barofishserver.grade.domain.Grade;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ReviewDtoV2 {

    private String userId;
    private String userName;
    private String userGrade;
    private String productName;
    private String reviewContent;
    private Timestamp createdAt;
    private String image;
    private Long likeSum;

    @Override
    public String toString() {
        return "ReviewDtoV2{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userGrade='" + userGrade + '\'' +
                ", productName='" + productName + '\'' +
                ", reviewContent='" + reviewContent + '\'' +
                ", createdAt=" + createdAt +
                ", image='" + image + '\'' +
                ", likeSum=" + likeSum +
                '}';
    }
}
