package com.matsinger.barofishserver.domain.payment.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPayments is a Querydsl query type for Payments
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPayments extends EntityPathBase<Payments> {

    private static final long serialVersionUID = -822851427L;

    public static final QPayments payments = new QPayments("payments");

    public final StringPath applyNum = createString("applyNum");

    public final StringPath buyerAddress = createString("buyerAddress");

    public final StringPath buyerEmail = createString("buyerEmail");

    public final StringPath buyerName = createString("buyerName");

    public final StringPath buyerTel = createString("buyerTel");

    public final StringPath embPgProvider = createString("embPgProvider");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath impUid = createString("impUid");

    public final StringPath merchantUid = createString("merchantUid");

    public final StringPath name = createString("name");

    public final StringPath orderId = createString("orderId");

    public final NumberPath<Integer> paidAmount = createNumber("paidAmount", Integer.class);

    public final DateTimePath<java.sql.Timestamp> paidAt = createDateTime("paidAt", java.sql.Timestamp.class);

    public final StringPath payMethod = createString("payMethod");

    public final StringPath pgProvider = createString("pgProvider");

    public final StringPath pgTid = createString("pgTid");

    public final StringPath receiptUrl = createString("receiptUrl");

    public final EnumPath<PaymentState> status = createEnum("status", PaymentState.class);

    public final StringPath vbankCode = createString("vbankCode");

    public final DateTimePath<java.sql.Timestamp> vbankDate = createDateTime("vbankDate", java.sql.Timestamp.class);

    public final StringPath vbankHolder = createString("vbankHolder");

    public final StringPath vbankName = createString("vbankName");

    public final StringPath vbankNum = createString("vbankNum");

    public QPayments(String variable) {
        super(Payments.class, forVariable(variable));
    }

    public QPayments(Path<? extends Payments> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPayments(PathMetadata metadata) {
        super(Payments.class, metadata);
    }

}

