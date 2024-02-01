package com.matsinger.barofishserver.domain.review.dto.v2;

import com.matsinger.barofishserver.domain.review.domain.ReviewEvaluationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class AdminReviewDto {

    private Integer reviewId;
    private String storeName;
    private String productTitle;
    private String userNickname;
    private String userEmail;
    private List<ReviewEvaluationType> evaluations;
    private String content;
    private String images;
    private String[] imageUrls;
    private Timestamp createdAt;

    private Long likeSum;

    public void setEvaluations(List<ReviewEvaluationType> evaluations) {
        this.evaluations = evaluations;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void deleteImages() {
        this.images = null;
    }
}
