package com.matsinger.barofishserver.domain.userauth.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserAuth is a Querydsl query type for UserAuth
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserAuth extends EntityPathBase<UserAuth> {

    private static final long serialVersionUID = -902294380L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserAuth userAuth = new QUserAuth("userAuth");

    public final com.matsinger.barofishserver.global.config.QBaseTimeEntity _super = new com.matsinger.barofishserver.global.config.QBaseTimeEntity(this);

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final StringPath loginId = createString("loginId");

    public final EnumPath<LoginType> loginType = createEnum("loginType", LoginType.class);

    public final StringPath password = createString("password");

    //inherited
    public final DateTimePath<java.sql.Timestamp> updatedAt = _super.updatedAt;

    public final com.matsinger.barofishserver.domain.user.domain.QUser user;

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QUserAuth(String variable) {
        this(UserAuth.class, forVariable(variable), INITS);
    }

    public QUserAuth(Path<? extends UserAuth> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserAuth(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserAuth(PathMetadata metadata, PathInits inits) {
        this(UserAuth.class, metadata, inits);
    }

    public QUserAuth(Class<? extends UserAuth> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.matsinger.barofishserver.domain.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

