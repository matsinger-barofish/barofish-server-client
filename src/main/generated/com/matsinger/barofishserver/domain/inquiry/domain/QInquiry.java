package com.matsinger.barofishserver.domain.inquiry.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QInquiry is a Querydsl query type for Inquiry
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInquiry extends EntityPathBase<Inquiry> {

    private static final long serialVersionUID = -1924370314L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInquiry inquiry = new QInquiry("inquiry");

    public final StringPath answer = createString("answer");

    public final DateTimePath<java.sql.Timestamp> answeredAt = createDateTime("answeredAt", java.sql.Timestamp.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final BooleanPath isSecret = createBoolean("isSecret");

    public final com.matsinger.barofishserver.domain.product.domain.QProduct product;

    public final NumberPath<Integer> productId = createNumber("productId", Integer.class);

    public final EnumPath<InquiryType> type = createEnum("type", InquiryType.class);

    public final com.matsinger.barofishserver.domain.user.domain.QUser user;

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QInquiry(String variable) {
        this(Inquiry.class, forVariable(variable), INITS);
    }

    public QInquiry(Path<? extends Inquiry> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QInquiry(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QInquiry(PathMetadata metadata, PathInits inits) {
        this(Inquiry.class, metadata, inits);
    }

    public QInquiry(Class<? extends Inquiry> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new com.matsinger.barofishserver.domain.product.domain.QProduct(forProperty("product"), inits.get("product")) : null;
        this.user = inits.isInitialized("user") ? new com.matsinger.barofishserver.domain.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

