package com.matsinger.barofishserver.domain.tastingNote.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTastingNote is a Querydsl query type for TastingNote
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTastingNote extends EntityPathBase<TastingNote> {

    private static final long serialVersionUID = 1560974742L;

    public static final QTastingNote tastingNote = new QTastingNote("tastingNote");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> orderProductInfoId = createNumber("orderProductInfoId", Integer.class);

    public final NumberPath<Integer> productId = createNumber("productId", Integer.class);

    public final NumberPath<Double> taste1 = createNumber("taste1", Double.class);

    public final NumberPath<Double> taste2 = createNumber("taste2", Double.class);

    public final NumberPath<Double> taste3 = createNumber("taste3", Double.class);

    public final NumberPath<Double> taste4 = createNumber("taste4", Double.class);

    public final NumberPath<Double> taste5 = createNumber("taste5", Double.class);

    public final NumberPath<Double> texture1 = createNumber("texture1", Double.class);

    public final NumberPath<Double> texture10 = createNumber("texture10", Double.class);

    public final NumberPath<Double> texture2 = createNumber("texture2", Double.class);

    public final NumberPath<Double> texture3 = createNumber("texture3", Double.class);

    public final NumberPath<Double> texture4 = createNumber("texture4", Double.class);

    public final NumberPath<Double> texture5 = createNumber("texture5", Double.class);

    public final NumberPath<Double> texture6 = createNumber("texture6", Double.class);

    public final NumberPath<Double> texture7 = createNumber("texture7", Double.class);

    public final NumberPath<Double> texture8 = createNumber("texture8", Double.class);

    public final NumberPath<Double> texture9 = createNumber("texture9", Double.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QTastingNote(String variable) {
        super(TastingNote.class, forVariable(variable));
    }

    public QTastingNote(Path<? extends TastingNote> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTastingNote(PathMetadata metadata) {
        super(TastingNote.class, metadata);
    }

}

