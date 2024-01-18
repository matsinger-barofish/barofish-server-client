package com.matsinger.barofishserver.domain.admin.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAdminAuth is a Querydsl query type for AdminAuth
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdminAuth extends EntityPathBase<AdminAuth> {

    private static final long serialVersionUID = -1665939330L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAdminAuth adminAuth = new QAdminAuth("adminAuth");

    public final BooleanPath accessBoard = createBoolean("accessBoard");

    public final BooleanPath accessOrder = createBoolean("accessOrder");

    public final BooleanPath accessProduct = createBoolean("accessProduct");

    public final BooleanPath accessPromotion = createBoolean("accessPromotion");

    public final BooleanPath accessSetting = createBoolean("accessSetting");

    public final BooleanPath accessSettlement = createBoolean("accessSettlement");

    public final BooleanPath accessUser = createBoolean("accessUser");

    public final QAdmin admin;

    public final NumberPath<Integer> adminId = createNumber("adminId", Integer.class);

    public QAdminAuth(String variable) {
        this(AdminAuth.class, forVariable(variable), INITS);
    }

    public QAdminAuth(Path<? extends AdminAuth> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAdminAuth(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAdminAuth(PathMetadata metadata, PathInits inits) {
        this(AdminAuth.class, metadata, inits);
    }

    public QAdminAuth(Class<? extends AdminAuth> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.admin = inits.isInitialized("admin") ? new QAdmin(forProperty("admin"), inits.get("admin")) : null;
    }

}

