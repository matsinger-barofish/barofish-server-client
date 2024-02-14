package com.matsinger.barofishserver.domain.coupon.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCoupon is a Querydsl query type for Coupon
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCoupon extends EntityPathBase<Coupon> {

    private static final long serialVersionUID = -1914968998L;

    public static final QCoupon coupon = new QCoupon("coupon");

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    public final DateTimePath<java.sql.Timestamp> endAt = createDateTime("endAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> minPrice = createNumber("minPrice", Integer.class);

    public final EnumPath<CouponPublicType> publicType = createEnum("publicType", CouponPublicType.class);

    public final DateTimePath<java.sql.Timestamp> startAt = createDateTime("startAt", java.sql.Timestamp.class);

    public final EnumPath<CouponState> state = createEnum("state", CouponState.class);

    public final StringPath title = createString("title");

    public final EnumPath<CouponType> type = createEnum("type", CouponType.class);

    public QCoupon(String variable) {
        super(Coupon.class, forVariable(variable));
    }

    public QCoupon(Path<? extends Coupon> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCoupon(PathMetadata metadata) {
        super(Coupon.class, metadata);
    }

}

