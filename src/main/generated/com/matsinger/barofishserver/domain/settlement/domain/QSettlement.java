package com.matsinger.barofishserver.domain.settlement.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSettlement is a Querydsl query type for Settlement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSettlement extends EntityPathBase<Settlement> {

    private static final long serialVersionUID = 681339040L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSettlement settlement = new QSettlement("settlement");

    public final StringPath cancelReason = createString("cancelReason");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.sql.Timestamp> settledAt = createDateTime("settledAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> settlementAmount = createNumber("settlementAmount", Integer.class);

    public final EnumPath<SettlementState> state = createEnum("state", SettlementState.class);

    public final com.matsinger.barofishserver.domain.store.domain.QStore store;

    public final NumberPath<Integer> storeId = createNumber("storeId", Integer.class);

    public QSettlement(String variable) {
        this(Settlement.class, forVariable(variable), INITS);
    }

    public QSettlement(Path<? extends Settlement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSettlement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSettlement(PathMetadata metadata, PathInits inits) {
        this(Settlement.class, metadata, inits);
    }

    public QSettlement(Class<? extends Settlement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new com.matsinger.barofishserver.domain.store.domain.QStore(forProperty("store"), inits.get("store")) : null;
    }

}

