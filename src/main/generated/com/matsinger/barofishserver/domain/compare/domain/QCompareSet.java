package com.matsinger.barofishserver.domain.compare.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCompareSet is a Querydsl query type for CompareSet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCompareSet extends EntityPathBase<CompareSet> {

    private static final long serialVersionUID = -627970100L;

    public static final QCompareSet compareSet = new QCompareSet("compareSet");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QCompareSet(String variable) {
        super(CompareSet.class, forVariable(variable));
    }

    public QCompareSet(Path<? extends CompareSet> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCompareSet(PathMetadata metadata) {
        super(CompareSet.class, metadata);
    }

}

