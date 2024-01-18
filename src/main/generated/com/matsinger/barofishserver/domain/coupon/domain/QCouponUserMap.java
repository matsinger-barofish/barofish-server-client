package com.matsinger.barofishserver.domain.coupon.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCouponUserMap is a Querydsl query type for CouponUserMap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCouponUserMap extends EntityPathBase<CouponUserMap> {

    private static final long serialVersionUID = -1588026409L;

    public static final QCouponUserMap couponUserMap = new QCouponUserMap("couponUserMap");

    public final NumberPath<Integer> couponId = createNumber("couponId", Integer.class);

    public final BooleanPath isUsed = createBoolean("isUsed");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QCouponUserMap(String variable) {
        super(CouponUserMap.class, forVariable(variable));
    }

    public QCouponUserMap(Path<? extends CouponUserMap> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCouponUserMap(PathMetadata metadata) {
        super(CouponUserMap.class, metadata);
    }

}

