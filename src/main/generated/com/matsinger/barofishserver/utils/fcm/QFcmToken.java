package com.matsinger.barofishserver.utils.fcm;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QFcmToken is a Querydsl query type for FcmToken
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFcmToken extends EntityPathBase<FcmToken> {

    private static final long serialVersionUID = -520049124L;

    public static final QFcmToken fcmToken = new QFcmToken("fcmToken");

    public final StringPath token = createString("token");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QFcmToken(String variable) {
        super(FcmToken.class, forVariable(variable));
    }

    public QFcmToken(Path<? extends FcmToken> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFcmToken(PathMetadata metadata) {
        super(FcmToken.class, metadata);
    }

}

