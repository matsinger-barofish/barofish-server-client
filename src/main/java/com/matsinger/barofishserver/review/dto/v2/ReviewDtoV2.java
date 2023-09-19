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
    private Integer likeSum;
}
