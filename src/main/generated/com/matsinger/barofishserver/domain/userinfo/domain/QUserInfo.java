package com.matsinger.barofishserver.domain.userinfo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserInfo is a Querydsl query type for UserInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserInfo extends EntityPathBase<UserInfo> {

    private static final long serialVersionUID = -598976864L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserInfo userInfo = new QUserInfo("userInfo");

    public final StringPath email = createString("email");

    public final com.matsinger.barofishserver.domain.grade.domain.QGrade grade;

    public final BooleanPath isAgreeMarketing = createBoolean("isAgreeMarketing");

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath phone = createString("phone");

    public final NumberPath<Integer> point = createNumber("point", Integer.class);

    public final StringPath profileImage = createString("profileImage");

    public final com.matsinger.barofishserver.domain.user.domain.QUser user;

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QUserInfo(String variable) {
        this(UserInfo.class, forVariable(variable), INITS);
    }

    public QUserInfo(Path<? extends UserInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserInfo(PathMetadata metadata, PathInits inits) {
        this(UserInfo.class, metadata, inits);
    }

    public QUserInfo(Class<? extends UserInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.grade = inits.isInitialized("grade") ? new com.matsinger.barofishserver.domain.grade.domain.QGrade(forProperty("grade")) : null;
        this.user = inits.isInitialized("user") ? new com.matsinger.barofishserver.domain.user.domain.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

