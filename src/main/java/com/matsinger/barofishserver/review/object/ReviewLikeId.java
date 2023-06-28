package com.matsinger.barofishserver.review.object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLikeId implements Serializable {
    private Integer reviewId;
    private Integer userId;
}
