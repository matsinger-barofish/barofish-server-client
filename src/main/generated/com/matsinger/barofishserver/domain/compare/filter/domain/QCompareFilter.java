package com.matsinger.barofishserver.domain.compare.filter.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCompareFilter is a Querydsl query type for CompareFilter
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCompareFilter extends EntityPathBase<CompareFilter> {

    private static final long serialVersionUID = -1967848850L;

    public static final QCompareFilter compareFilter = new QCompareFilter("compareFilter");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public QCompareFilter(String variable) {
        super(CompareFilter.class, forVariable(variable));
    }

    public QCompareFilter(Path<? extends CompareFilter> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCompareFilter(PathMetadata metadata) {
        super(CompareFilter.class, metadata);
    }

}

