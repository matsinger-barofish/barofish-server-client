package com.matsinger.barofishserver.domain.store.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStore is a Querydsl query type for Store
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStore extends EntityPathBase<Store> {

    private static final long serialVersionUID = -477405450L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStore store = new QStore("store");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.sql.Timestamp> joinAt = createDateTime("joinAt", java.sql.Timestamp.class);

    public final StringPath loginId = createString("loginId");

    public final StringPath password = createString("password");

    public final ListPath<com.matsinger.barofishserver.domain.review.domain.Review, com.matsinger.barofishserver.domain.review.domain.QReview> reviews = this.<com.matsinger.barofishserver.domain.review.domain.Review, com.matsinger.barofishserver.domain.review.domain.QReview>createList("reviews", com.matsinger.barofishserver.domain.review.domain.Review.class, com.matsinger.barofishserver.domain.review.domain.QReview.class, PathInits.DIRECT2);

    public final EnumPath<StoreState> state = createEnum("state", StoreState.class);

    public final QStoreInfo storeInfo;

    public QStore(String variable) {
        this(Store.class, forVariable(variable), INITS);
    }

    public QStore(Path<? extends Store> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStore(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStore(PathMetadata metadata, PathInits inits) {
        this(Store.class, metadata, inits);
    }

    public QStore(Class<? extends Store> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.storeInfo = inits.isInitialized("storeInfo") ? new QStoreInfo(forProperty("storeInfo"), inits.get("storeInfo")) : null;
    }

}

