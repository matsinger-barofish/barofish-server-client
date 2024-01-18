package com.matsinger.barofishserver.domain.user.paymentMethod.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPaymentMethod is a Querydsl query type for PaymentMethod
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentMethod extends EntityPathBase<PaymentMethod> {

    private static final long serialVersionUID = 1014413333L;

    public static final QPaymentMethod paymentMethod = new QPaymentMethod("paymentMethod");

    public final StringPath birth = createString("birth");

    public final StringPath cardName = createString("cardName");

    public final StringPath cardNo = createString("cardNo");

    public final StringPath customerUid = createString("customerUid");

    public final StringPath expiryAt = createString("expiryAt");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final StringPath passwordTwoDigit = createString("passwordTwoDigit");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QPaymentMethod(String variable) {
        super(PaymentMethod.class, forVariable(variable));
    }

    public QPaymentMethod(Path<? extends PaymentMethod> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPaymentMethod(PathMetadata metadata) {
        super(PaymentMethod.class, metadata);
    }

}

