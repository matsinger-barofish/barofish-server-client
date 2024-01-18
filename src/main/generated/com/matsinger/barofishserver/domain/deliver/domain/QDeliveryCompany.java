package com.matsinger.barofishserver.domain.deliver.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDeliveryCompany is a Querydsl query type for DeliveryCompany
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeliveryCompany extends EntityPathBase<DeliveryCompany> {

    private static final long serialVersionUID = -2076509062L;

    public static final QDeliveryCompany deliveryCompany = new QDeliveryCompany("deliveryCompany");

    public final StringPath code = createString("code");

    public final StringPath name = createString("name");

    public QDeliveryCompany(String variable) {
        super(DeliveryCompany.class, forVariable(variable));
    }

    public QDeliveryCompany(Path<? extends DeliveryCompany> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDeliveryCompany(PathMetadata metadata) {
        super(DeliveryCompany.class, metadata);
    }

}

