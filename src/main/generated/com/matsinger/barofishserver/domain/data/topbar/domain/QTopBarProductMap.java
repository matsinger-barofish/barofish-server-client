package com.matsinger.barofishserver.domain.data.topbar.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTopBarProductMap is a Querydsl query type for TopBarProductMap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTopBarProductMap extends EntityPathBase<TopBarProductMap> {

    private static final long serialVersionUID = 289632471L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTopBarProductMap topBarProductMap = new QTopBarProductMap("topBarProductMap");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.matsinger.barofishserver.domain.product.domain.QProduct product;

    public final QTopBar topBar;

    public QTopBarProductMap(String variable) {
        this(TopBarProductMap.class, forVariable(variable), INITS);
    }

    public QTopBarProductMap(Path<? extends TopBarProductMap> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTopBarProductMap(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTopBarProductMap(PathMetadata metadata, PathInits inits) {
        this(TopBarProductMap.class, metadata, inits);
    }

    public QTopBarProductMap(Class<? extends TopBarProductMap> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new com.matsinger.barofishserver.domain.product.domain.QProduct(forProperty("product"), inits.get("product")) : null;
        this.topBar = inits.isInitialized("topBar") ? new QTopBar(forProperty("topBar")) : null;
    }

}

