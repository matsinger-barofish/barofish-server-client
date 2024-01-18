package com.matsinger.barofishserver.domain.report.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QReport is a Querydsl query type for Report
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QReport extends EntityPathBase<Report> {

    private static final long serialVersionUID = -921702922L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QReport report = new QReport("report");

    public final DateTimePath<java.sql.Timestamp> confirmAt = createDateTime("confirmAt", java.sql.Timestamp.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final com.matsinger.barofishserver.domain.review.domain.QReview review;

    public final NumberPath<Integer> reviewId = createNumber("reviewId", Integer.class);

    public final com.matsinger.barofishserver.domain.user.domain.QUser user;

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QReport(String variable) {
        this(Report.class, forVariable(variable), INITS);
    }

    public QReport(Path<? extends Report> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QReport(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QReport(PathMetadata metadata, PathInits inits) {
        this(Report.class, metadata, inits);
    }

    public QReport(Class<? extends Report> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.review = inits.isInitialized("review") ? new com.matsinger.barofishserver.domain.review.domain.QReview(forProperty("review"), inits.get("review")) : null;
        this.user = inits.isInitialized("user") ? new com.matsinger.barofishserver.domain.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

