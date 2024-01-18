package com.matsinger.barofishserver.domain.data.topbar.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTopBar is a Querydsl query type for TopBar
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTopBar extends EntityPathBase<TopBar> {

    private static final long serialVersionUID = 1655251786L;

    public static final QTopBar topBar = new QTopBar("topBar");

    public final ListPath<TopBarProductMap, QTopBarProductMap> curationProductMaps = this.<TopBarProductMap, QTopBarProductMap>createList("curationProductMaps", TopBarProductMap.class, QTopBarProductMap.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public QTopBar(String variable) {
        super(TopBar.class, forVariable(variable));
    }

    public QTopBar(Path<? extends TopBar> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTopBar(PathMetadata metadata) {
        super(TopBar.class, metadata);
    }

}

