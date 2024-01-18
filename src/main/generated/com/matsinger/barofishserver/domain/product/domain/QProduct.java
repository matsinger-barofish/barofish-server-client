package com.matsinger.barofishserver.domain.product.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProduct is a Querydsl query type for Product
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProduct extends EntityPathBase<Product> {

    private static final long serialVersionUID = -2083996810L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProduct product = new QProduct("product");

    public final com.matsinger.barofishserver.domain.category.domain.QCategory category;

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> deliverBoxPerAmount = createNumber("deliverBoxPerAmount", Integer.class);

    public final NumberPath<Integer> deliverFee = createNumber("deliverFee", Integer.class);

    public final EnumPath<ProductDeliverFeeType> deliverFeeType = createEnum("deliverFeeType", ProductDeliverFeeType.class);

    public final StringPath deliveryInfo = createString("deliveryInfo");

    public final StringPath descriptionImages = createString("descriptionImages");

    public final NumberPath<Double> difficultyLevelOfTrimming = createNumber("difficultyLevelOfTrimming", Double.class);

    public final NumberPath<Integer> discountRate = createNumber("discountRate", Integer.class);

    public final NumberPath<Integer> expectedDeliverDay = createNumber("expectedDeliverDay", Integer.class);

    public final StringPath forwardingTime = createString("forwardingTime");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath images = createString("images");

    public final StringPath itemCode = createString("itemCode");

    public final NumberPath<Integer> minOrderPrice = createNumber("minOrderPrice", Integer.class);

    public final BooleanPath needTaxation = createBoolean("needTaxation");

    public final NumberPath<Integer> originPrice = createNumber("originPrice", Integer.class);

    public final NumberPath<Float> pointRate = createNumber("pointRate", Float.class);

    public final DateTimePath<java.sql.Timestamp> promotionEndAt = createDateTime("promotionEndAt", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> promotionStartAt = createDateTime("promotionStartAt", java.sql.Timestamp.class);

    public final StringPath recommendedCookingWay = createString("recommendedCookingWay");

    public final NumberPath<Integer> representOptionItemId = createNumber("representOptionItemId", Integer.class);

    public final ListPath<com.matsinger.barofishserver.domain.review.domain.Review, com.matsinger.barofishserver.domain.review.domain.QReview> reviews = this.<com.matsinger.barofishserver.domain.review.domain.Review, com.matsinger.barofishserver.domain.review.domain.QReview>createList("reviews", com.matsinger.barofishserver.domain.review.domain.Review.class, com.matsinger.barofishserver.domain.review.domain.QReview.class, PathInits.DIRECT2);

    public final EnumPath<ProductState> state = createEnum("state", ProductState.class);

    public final com.matsinger.barofishserver.domain.store.domain.QStore store;

    public final NumberPath<Integer> storeId = createNumber("storeId", Integer.class);

    public final NumberPath<Double> theScentOfTheSea = createNumber("theScentOfTheSea", Double.class);

    public final StringPath title = createString("title");

    public QProduct(String variable) {
        this(Product.class, forVariable(variable), INITS);
    }

    public QProduct(Path<? extends Product> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProduct(PathMetadata metadata, PathInits inits) {
        this(Product.class, metadata, inits);
    }

    public QProduct(Class<? extends Product> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.matsinger.barofishserver.domain.category.domain.QCategory(forProperty("category"), inits.get("category")) : null;
        this.store = inits.isInitialized("store") ? new com.matsinger.barofishserver.domain.store.domain.QStore(forProperty("store"), inits.get("store")) : null;
    }

}

