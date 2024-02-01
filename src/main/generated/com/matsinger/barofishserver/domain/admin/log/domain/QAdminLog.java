package com.matsinger.barofishserver.domain.admin.log.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAdminLog is a Querydsl query type for AdminLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAdminLog extends EntityPathBase<AdminLog> {

    private static final long serialVersionUID = -1685860476L;

    public static final QAdminLog adminLog = new QAdminLog("adminLog");

    public final NumberPath<Integer> adminId = createNumber("adminId", Integer.class);

    public final StringPath content = createString("content");

    public final DateTimePath<java.sql.Timestamp> createdAt = createDateTime("createdAt", java.sql.Timestamp.class);

    public final StringPath id = createString("id");

    public final StringPath targetId = createString("targetId");

    public final EnumPath<AdminLogType> type = createEnum("type", AdminLogType.class);

    public QAdminLog(String variable) {
        super(AdminLog.class, forVariable(variable));
    }

    public QAdminLog(Path<? extends AdminLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAdminLog(PathMetadata metadata) {
        super(AdminLog.class, metadata);
    }

}

