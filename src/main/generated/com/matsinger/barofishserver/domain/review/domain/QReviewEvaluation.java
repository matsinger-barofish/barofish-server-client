package com.matsinger.barofishserver.domain.review.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReviewEvaluation is a Querydsl query type for ReviewEvaluation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReviewEvaluation extends EntityPathBase<ReviewEvaluation> {

    private static final long serialVersionUID = 1239290138L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReviewEvaluation reviewEvaluation = new QReviewEvaluation("reviewEvaluation");

    public final EnumPath<ReviewEvaluationType> evaluation = createEnum("evaluation", ReviewEvaluationType.class);

    public final QReview review;

    public final NumberPath<Integer> reviewId = createNumber("reviewId", Integer.class);

    public QReviewEvaluation(String variable) {
        this(ReviewEvaluation.class, forVariable(variable), INITS);
    }

    public QReviewEvaluation(Path<? extends ReviewEvaluation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReviewEvaluation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReviewEvaluation(PathMetadata metadata, PathInits inits) {
        this(ReviewEvaluation.class, metadata, inits);
    }

    public QReviewEvaluation(Class<? extends ReviewEvaluation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.review = inits.isInitialized("review") ? new QReview(forProperty("review"), inits.get("review")) : null;
    }

}

