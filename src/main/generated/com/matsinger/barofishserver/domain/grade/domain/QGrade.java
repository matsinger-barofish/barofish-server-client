package com.matsinger.barofishserver.domain.grade.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGrade is a Querydsl query type for Grade
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGrade extends EntityPathBase<Grade> {

    private static final long serialVersionUID = -893591882L;

    public static final QGrade grade = new QGrade("grade");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> minOrderCount = createNumber("minOrderCount", Integer.class);

    public final NumberPath<Integer> minOrderPrice = createNumber("minOrderPrice", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Float> pointRate = createNumber("pointRate", Float.class);

    public QGrade(String variable) {
        super(Grade.class, forVariable(variable));
    }

    public QGrade(Path<? extends Grade> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGrade(PathMetadata metadata) {
        super(Grade.class, metadata);
    }

}

