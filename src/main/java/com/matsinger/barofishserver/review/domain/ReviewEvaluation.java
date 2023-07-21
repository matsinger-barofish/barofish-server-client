package com.matsinger.barofishserver.review.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "review_evaluation", schema = "barofish_dev", catalog = "")
@IdClass(ReviewEvaluationId.class)
public class ReviewEvaluation {
    @Column(name = "review_id", nullable = false)
    @Id
    private int reviewId;
    @Column(name = "evaluation", nullable = false)
    @Id
    @Enumerated(EnumType.STRING)
    private ReviewEvaluationType evaluation;

    @ManyToOne
    @JoinColumn(name = "review_id", updatable = false, insertable = false)
    private Review review;
}
