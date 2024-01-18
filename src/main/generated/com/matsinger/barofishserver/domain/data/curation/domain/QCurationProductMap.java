package com.matsinger.barofishserver.domain.data.curation.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCurationProductMap is a Querydsl query type for CurationProductMap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCurationProductMap extends EntityPathBase<CurationProductMap> {

    private static final long serialVersionUID = -246712795L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCurationProductMap curationProductMap = new QCurationProductMap("curationProductMap");

    public final QCuration curation;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.matsinger.barofishserver.domain.product.domain.QProduct product;

    public QCurationProductMap(String variable) {
        this(CurationProductMap.class, forVariable(variable), INITS);
    }

    public QCurationProductMap(Path<? extends CurationProductMap> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCurationProductMap(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCurationProductMap(PathMetadata metadata, PathInits inits) {
        this(CurationProductMap.class, metadata, inits);
    }

    public QCurationProductMap(Class<? extends CurationProductMap> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.curation = inits.isInitialized("curation") ? new QCuration(forProperty("curation")) : null;
        this.product = inits.isInitialized("product") ? new com.matsinger.barofishserver.domain.product.domain.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

