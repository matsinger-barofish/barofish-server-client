package com.matsinger.barofishserver.domain.product.difficultDeliverAddress.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDifficultDeliverAddress is a Querydsl query type for DifficultDeliverAddress
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDifficultDeliverAddress extends EntityPathBase<DifficultDeliverAddress> {

    private static final long serialVersionUID = -1915669611L;

    public static final QDifficultDeliverAddress difficultDeliverAddress = new QDifficultDeliverAddress("difficultDeliverAddress");

    public final StringPath bcode = createString("bcode");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> productId = createNumber("productId", Integer.class);

    public QDifficultDeliverAddress(String variable) {
        super(DifficultDeliverAddress.class, forVariable(variable));
    }

    public QDifficultDeliverAddress(Path<? extends DifficultDeliverAddress> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDifficultDeliverAddress(PathMetadata metadata) {
        super(DifficultDeliverAddress.class, metadata);
    }

}

