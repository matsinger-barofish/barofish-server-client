package com.matsinger.barofishserver.domain.compare.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSaveProduct is a Querydsl query type for SaveProduct
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSaveProduct extends EntityPathBase<SaveProduct> {

    private static final long serialVersionUID = -1493635069L;

    public static final QSaveProduct saveProduct = new QSaveProduct("saveProduct");

    public final NumberPath<Integer> productId = createNumber("productId", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QSaveProduct(String variable) {
        super(SaveProduct.class, forVariable(variable));
    }

    public QSaveProduct(Path<? extends SaveProduct> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSaveProduct(PathMetadata metadata) {
        super(SaveProduct.class, metadata);
    }

}

