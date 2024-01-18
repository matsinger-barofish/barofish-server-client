package com.matsinger.barofishserver.domain.data.tip.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTip is a Querydsl query type for Tip
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTip extends EntityPathBase<Tip> {

    private static final long serialVersionUID = -1598533482L;

    public static final QTip tip = new QTip("tip");

    public final StringPath content = createString("content");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final StringPath description = createString("description");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath image = createString("image");

    public final StringPath imageDetail = createString("imageDetail");

    public final EnumPath<TipState> state = createEnum("state", TipState.class);

    public final StringPath title = createString("title");

    public final EnumPath<TipType> type = createEnum("type", TipType.class);

    public QTip(String variable) {
        super(Tip.class, forVariable(variable));
    }

    public QTip(Path<? extends Tip> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTip(PathMetadata metadata) {
        super(Tip.class, metadata);
    }

}

