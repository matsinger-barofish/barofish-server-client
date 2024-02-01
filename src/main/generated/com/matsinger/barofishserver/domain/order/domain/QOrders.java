package com.matsinger.barofishserver.domain.order.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrders is a Querydsl query type for Orders
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrders extends EntityPathBase<Orders> {

    private static final long serialVersionUID = 1804471069L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrders orders = new QOrders("orders");

    public final StringPath bankAccount = createString("bankAccount");

    public final StringPath bankCode = createString("bankCode");

    public final StringPath bankHolder = createString("bankHolder");

    public final StringPath bankName = createString("bankName");

    public final NumberPath<Integer> couponDiscount = createNumber("couponDiscount", Integer.class);

    public final NumberPath<Integer> couponId = createNumber("couponId", Integer.class);

    public final QOrderDeliverPlace deliverPlace;

    public final StringPath id = createString("id");

    public final StringPath impUid = createString("impUid");

    public final DateTimePath<java.sql.Timestamp> orderedAt = createDateTime("orderedAt", java.sql.Timestamp.class);

    public final StringPath ordererName = createString("ordererName");

    public final StringPath ordererTel = createString("ordererTel");

    public final NumberPath<Integer> originTotalPrice = createNumber("originTotalPrice", Integer.class);

    public final EnumPath<OrderPaymentWay> paymentWay = createEnum("paymentWay", OrderPaymentWay.class);

    public final ListPath<com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo, com.matsinger.barofishserver.domain.order.orderprductinfo.domain.QOrderProductInfo> productInfos = this.<com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo, com.matsinger.barofishserver.domain.order.orderprductinfo.domain.QOrderProductInfo>createList("productInfos", com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo.class, com.matsinger.barofishserver.domain.order.orderprductinfo.domain.QOrderProductInfo.class, PathInits.DIRECT2);

    public final EnumPath<OrderState> state = createEnum("state", OrderState.class);

    public final NumberPath<Integer> totalPrice = createNumber("totalPrice", Integer.class);

    public final NumberPath<Integer> usePoint = createNumber("usePoint", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QOrders(String variable) {
        this(Orders.class, forVariable(variable), INITS);
    }

    public QOrders(Path<? extends Orders> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrders(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrders(PathMetadata metadata, PathInits inits) {
        this(Orders.class, metadata, inits);
    }

    public QOrders(Class<? extends Orders> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.deliverPlace = inits.isInitialized("deliverPlace") ? new QOrderDeliverPlace(forProperty("deliverPlace"), inits.get("deliverPlace")) : null;
    }

}

