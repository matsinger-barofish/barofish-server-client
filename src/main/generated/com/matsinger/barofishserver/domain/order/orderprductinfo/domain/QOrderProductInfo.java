package com.matsinger.barofishserver.domain.order.orderprductinfo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrderProductInfo is a Querydsl query type for OrderProductInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderProductInfo extends EntityPathBase<OrderProductInfo> {

    private static final long serialVersionUID = 509076121L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrderProductInfo orderProductInfo = new QOrderProductInfo("orderProductInfo");

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    public final EnumPath<OrderCancelReason> cancelReason = createEnum("cancelReason", OrderCancelReason.class);

    public final StringPath cancelReasonContent = createString("cancelReasonContent");

    public final StringPath deliverCompanyCode = createString("deliverCompanyCode");

    public final DateTimePath<java.sql.Timestamp> deliveryDoneAt = createDateTime("deliveryDoneAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> deliveryFee = createNumber("deliveryFee", Integer.class);

    public final EnumPath<com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType> deliveryFeeType = createEnum("deliveryFeeType", com.matsinger.barofishserver.domain.product.domain.ProductDeliverFeeType.class);

    public final DateTimePath<java.sql.Timestamp> finalConfirmedAt = createDateTime("finalConfirmedAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath invoiceCode = createString("invoiceCode");

    public final BooleanPath isSettled = createBoolean("isSettled");

    public final BooleanPath isTaxFree = createBoolean("isTaxFree");

    public final NumberPath<Integer> optionItemId = createNumber("optionItemId", Integer.class);

    public final com.matsinger.barofishserver.domain.order.domain.QOrders order;

    public final StringPath orderId = createString("orderId");

    public final NumberPath<Integer> originPrice = createNumber("originPrice", Integer.class);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public final com.matsinger.barofishserver.domain.product.domain.QProduct product;

    public final NumberPath<Integer> productId = createNumber("productId", Integer.class);

    public final DateTimePath<java.sql.Timestamp> settledAt = createDateTime("settledAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> settlePrice = createNumber("settlePrice", Integer.class);

    public final EnumPath<OrderProductState> state = createEnum("state", OrderProductState.class);

    public final NumberPath<Integer> taxFreeAmount = createNumber("taxFreeAmount", Integer.class);

    public QOrderProductInfo(String variable) {
        this(OrderProductInfo.class, forVariable(variable), INITS);
    }

    public QOrderProductInfo(Path<? extends OrderProductInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrderProductInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrderProductInfo(PathMetadata metadata, PathInits inits) {
        this(OrderProductInfo.class, metadata, inits);
    }

    public QOrderProductInfo(Class<? extends OrderProductInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.order = inits.isInitialized("order") ? new com.matsinger.barofishserver.domain.order.domain.QOrders(forProperty("order"), inits.get("order")) : null;
        this.product = inits.isInitialized("product") ? new com.matsinger.barofishserver.domain.product.domain.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

