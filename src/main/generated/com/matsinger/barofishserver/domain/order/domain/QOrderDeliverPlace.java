package com.matsinger.barofishserver.domain.order.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrderDeliverPlace is a Querydsl query type for OrderDeliverPlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderDeliverPlace extends EntityPathBase<OrderDeliverPlace> {

    private static final long serialVersionUID = -1403409896L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrderDeliverPlace orderDeliverPlace = new QOrderDeliverPlace("orderDeliverPlace");

    public final StringPath address = createString("address");

    public final StringPath addressDetail = createString("addressDetail");

    public final StringPath bcode = createString("bcode");

    public final StringPath deliverMessage = createString("deliverMessage");

    public final StringPath name = createString("name");

    public final QOrders order;

    public final StringPath orderId = createString("orderId");

    public final StringPath postalCode = createString("postalCode");

    public final StringPath receiverName = createString("receiverName");

    public final StringPath tel = createString("tel");

    public QOrderDeliverPlace(String variable) {
        this(OrderDeliverPlace.class, forVariable(variable), INITS);
    }

    public QOrderDeliverPlace(Path<? extends OrderDeliverPlace> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrderDeliverPlace(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrderDeliverPlace(PathMetadata metadata, PathInits inits) {
        this(OrderDeliverPlace.class, metadata, inits);
    }

    public QOrderDeliverPlace(Class<? extends OrderDeliverPlace> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new QOrders(forProperty("order"), inits.get("order")) : null;
    }

}

