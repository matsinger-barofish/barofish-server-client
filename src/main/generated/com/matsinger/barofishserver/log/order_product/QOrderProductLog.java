package com.matsinger.barofishserver.log.order_product;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrderProductLog is a Querydsl query type for OrderProductLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderProductLog extends EntityPathBase<OrderProductLog> {

    private static final long serialVersionUID = 1883576849L;

    public static final QOrderProductLog orderProductLog = new QOrderProductLog("orderProductLog");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final StringPath orderProductId = createString("orderProductId");

    public final StringPath stateAfter = createString("stateAfter");

    public final StringPath stateBefore = createString("stateBefore");

    public QOrderProductLog(String variable) {
        super(OrderProductLog.class, forVariable(variable));
    }

    public QOrderProductLog(Path<? extends OrderProductLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrderProductLog(PathMetadata metadata) {
        super(OrderProductLog.class, metadata);
    }

}

