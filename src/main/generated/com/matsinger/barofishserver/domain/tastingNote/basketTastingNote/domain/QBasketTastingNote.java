package com.matsinger.barofishserver.domain.tastingNote.basketTastingNote.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBasketTastingNote is a Querydsl query type for BasketTastingNote
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBasketTastingNote extends EntityPathBase<BasketTastingNote> {

    private static final long serialVersionUID = -104778850L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBasketTastingNote basketTastingNote = new QBasketTastingNote("basketTastingNote");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final com.matsinger.barofishserver.domain.product.domain.QProduct product;

    public final com.matsinger.barofishserver.domain.user.domain.QUser user;

    public QBasketTastingNote(String variable) {
        this(BasketTastingNote.class, forVariable(variable), INITS);
    }

    public QBasketTastingNote(Path<? extends BasketTastingNote> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBasketTastingNote(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBasketTastingNote(PathMetadata metadata, PathInits inits) {
        this(BasketTastingNote.class, metadata, inits);
    }

    public QBasketTastingNote(Class<? extends BasketTastingNote> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new com.matsinger.barofishserver.domain.product.domain.QProduct(forProperty("product"), inits.get("product")) : null;
        this.user = inits.isInitialized("user") ? new com.matsinger.barofishserver.domain.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

