package com.matsinger.barofishserver.domain.category.filter.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCategoryFilterMap is a Querydsl query type for CategoryFilterMap
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategoryFilterMap extends EntityPathBase<CategoryFilterMap> {

    private static final long serialVersionUID = -1502852140L;

    public static final QCategoryFilterMap categoryFilterMap = new QCategoryFilterMap("categoryFilterMap");

    public final NumberPath<Integer> categoryId = createNumber("categoryId", Integer.class);

    public final NumberPath<Integer> compareFilterId = createNumber("compareFilterId", Integer.class);

    public QCategoryFilterMap(String variable) {
        super(CategoryFilterMap.class, forVariable(variable));
    }

    public QCategoryFilterMap(Path<? extends CategoryFilterMap> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCategoryFilterMap(PathMetadata metadata) {
        super(CategoryFilterMap.class, metadata);
    }

}

