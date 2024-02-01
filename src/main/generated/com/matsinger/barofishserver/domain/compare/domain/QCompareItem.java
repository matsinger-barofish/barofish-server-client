package com.matsinger.barofishserver.domain.compare.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCompareItem is a Querydsl query type for CompareItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCompareItem extends EntityPathBase<CompareItem> {

    private static final long serialVersionUID = 2007479529L;

    public static final QCompareItem compareItem = new QCompareItem("compareItem");

    public final NumberPath<Integer> compareSetId = createNumber("compareSetId", Integer.class);

    public final NumberPath<Integer> productId = createNumber("productId", Integer.class);

    public QCompareItem(String variable) {
        super(CompareItem.class, forVariable(variable));
    }

    public QCompareItem(Path<? extends CompareItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCompareItem(PathMetadata metadata) {
        super(CompareItem.class, metadata);
    }

}

