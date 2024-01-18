package com.matsinger.barofishserver.domain.searchFilter.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QProductSearchFilterMap is a Querydsl query type for ProductSearchFilterMap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProductSearchFilterMap extends EntityPathBase<ProductSearchFilterMap> {

    private static final long serialVersionUID = 1527380315L;

    public static final QProductSearchFilterMap productSearchFilterMap = new QProductSearchFilterMap("productSearchFilterMap");

    public final NumberPath<Integer> fieldId = createNumber("fieldId", Integer.class);

    public final NumberPath<Integer> productId = createNumber("productId", Integer.class);

    public QProductSearchFilterMap(String variable) {
        super(ProductSearchFilterMap.class, forVariable(variable));
    }

    public QProductSearchFilterMap(Path<? extends ProductSearchFilterMap> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProductSearchFilterMap(PathMetadata metadata) {
        super(ProductSearchFilterMap.class, metadata);
    }

}

