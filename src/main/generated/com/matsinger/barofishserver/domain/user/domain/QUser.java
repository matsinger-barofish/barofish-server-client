package com.matsinger.barofishserver.domain.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 212307364L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.sql.Timestamp> joinAt = createDateTime("joinAt", java.sql.Timestamp.class);

    public final EnumPath<UserState> state = createEnum("state", UserState.class);

    public final ListPath<com.matsinger.barofishserver.domain.userauth.domain.UserAuth, com.matsinger.barofishserver.domain.userauth.domain.QUserAuth> userAuth = this.<com.matsinger.barofishserver.domain.userauth.domain.UserAuth, com.matsinger.barofishserver.domain.userauth.domain.QUserAuth>createList("userAuth", com.matsinger.barofishserver.domain.userauth.domain.UserAuth.class, com.matsinger.barofishserver.domain.userauth.domain.QUserAuth.class, PathInits.DIRECT2);

    public final com.matsinger.barofishserver.domain.userinfo.domain.QUserInfo userInfo;

    public final DateTimePath<java.sql.Timestamp> withdrawAt = createDateTime("withdrawAt", java.sql.Timestamp.class);

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userInfo = inits.isInitialized("userInfo") ? new com.matsinger.barofishserver.domain.userinfo.domain.QUserInfo(forProperty("userInfo"), inits.get("userInfo")) : null;
    }

}

