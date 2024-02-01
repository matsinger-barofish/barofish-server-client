package com.matsinger.barofishserver.domain.store.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QStoreScrap is a Querydsl query type for StoreScrap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoreScrap extends EntityPathBase<StoreScrap> {

    private static final long serialVersionUID = -1419255173L;

    public static final QStoreScrap storeScrap = new QStoreScrap("storeScrap");

    public final NumberPath<Integer> storeId = createNumber("storeId", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QStoreScrap(String variable) {
        super(StoreScrap.class, forVariable(variable));
    }

    public QStoreScrap(Path<? extends StoreScrap> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStoreScrap(PathMetadata metadata) {
        super(StoreScrap.class, metadata);
    }

}

