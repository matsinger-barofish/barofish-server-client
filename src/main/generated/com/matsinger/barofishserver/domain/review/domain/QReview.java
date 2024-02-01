package com.matsinger.barofishserver.domain.review.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReview is a Querydsl query type for Review
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReview extends EntityPathBase<Review> {

    private static final long serialVersionUID = 430744382L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReview review = new QReview("review");

    public final StringPath content = createString("content");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final ListPath<ReviewEvaluation, QReviewEvaluation> evaluations = this.<ReviewEvaluation, QReviewEvaluation>createList("evaluations", ReviewEvaluation.class, QReviewEvaluation.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath images = createString("images");

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final com.matsinger.barofishserver.domain.order.orderprductinfo.domain.QOrderProductInfo orderProductInfo;

    public final NumberPath<Integer> orderProductInfoId = createNumber("orderProductInfoId", Integer.class);

    public final com.matsinger.barofishserver.domain.product.domain.QProduct product;

    public final NumberPath<Integer> productId = createNumber("productId", Integer.class);

    public final com.matsinger.barofishserver.domain.store.domain.QStore store;

    public final NumberPath<Integer> storeId = createNumber("storeId", Integer.class);

    public final com.matsinger.barofishserver.domain.user.domain.QUser user;

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QReview(String variable) {
        this(Review.class, forVariable(variable), INITS);
    }

    public QReview(Path<? extends Review> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReview(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReview(PathMetadata metadata, PathInits inits) {
        this(Review.class, metadata, inits);
    }

    public QReview(Class<? extends Review> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.orderProductInfo = inits.isInitialized("orderProductInfo") ? new com.matsinger.barofishserver.domain.order.orderprductinfo.domain.QOrderProductInfo(forProperty("orderProductInfo"), inits.get("orderProductInfo")) : null;
        this.product = inits.isInitialized("product") ? new com.matsinger.barofishserver.domain.product.domain.QProduct(forProperty("product"), inits.get("product")) : null;
        this.store = inits.isInitialized("store") ? new com.matsinger.barofishserver.domain.store.domain.QStore(forProperty("store"), inits.get("store")) : null;
        this.user = inits.isInitialized("user") ? new com.matsinger.barofishserver.domain.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

