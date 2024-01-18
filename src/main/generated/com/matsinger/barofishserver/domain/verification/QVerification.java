package com.matsinger.barofishserver.domain.verification;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QVerification is a Querydsl query type for Verification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVerification extends EntityPathBase<Verification> {

    private static final long serialVersionUID = -1848426756L;

    public static final QVerification verification = new QVerification("verification");

    public final DateTimePath<java.sql.Timestamp> createAt = createDateTime("createAt", java.sql.Timestamp.class);

    public final DateTimePath<java.sql.Timestamp> expiredAt = createDateTime("expiredAt", java.sql.Timestamp.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath target = createString("target");

    public final StringPath verificationNumber = createString("verificationNumber");

    public QVerification(String variable) {
        super(Verification.class, forVariable(variable));
    }

    public QVerification(Path<? extends Verification> path) {
        super(path.getType(), path.getMetadata());
    }

    public QVerification(PathMetadata metadata) {
        super(Verification.class, metadata);
    }

}

