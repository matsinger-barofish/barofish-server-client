package com.matsinger.barofishserver.domain.review.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@IdClass(value = ReviewLikeId.class)
@Table(name = "review_like", schema = "barofish_dev", catalog = "")
public class ReviewLike {

    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Id
    @Column(name = "review_id", nullable = false)
    private int reviewId;

}
