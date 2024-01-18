package com.matsinger.barofishserver.domain.searchFilter.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSearchFilter is a Querydsl query type for SearchFilter
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSearchFilter extends EntityPathBase<SearchFilter> {

    private static final long serialVersionUID = -485397234L;

    public static final QSearchFilter searchFilter = new QSearchFilter("searchFilter");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public QSearchFilter(String variable) {
        super(SearchFilter.class, forVariable(variable));
    }

    public QSearchFilter(Path<? extends SearchFilter> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSearchFilter(PathMetadata metadata) {
        super(SearchFilter.class, metadata);
    }

}

