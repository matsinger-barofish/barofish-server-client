package com.matsinger.barofishserver.domain.product.productfilter.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProductFilterValue is a Querydsl query type for ProductFilterValue
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductFilterValue extends EntityPathBase<ProductFilterValue> {

    private static final long serialVersionUID = -286308548L;

    public static final QProductFilterValue productFilterValue = new QProductFilterValue("productFilterValue");

    public final NumberPath<Integer> compareFilterId = createNumber("compareFilterId", Integer.class);

    public final NumberPath<Integer> productId = createNumber("productId", Integer.class);

    public final StringPath value = createString("value");

    public QProductFilterValue(String variable) {
        super(ProductFilterValue.class, forVariable(variable));
    }

    public QProductFilterValue(Path<? extends ProductFilterValue> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductFilterValue(PathMetadata metadata) {
        super(ProductFilterValue.class, metadata);
    }

}

