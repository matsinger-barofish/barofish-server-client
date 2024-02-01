package com.matsinger.barofishserver.domain.data.curation.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCuration is a Querydsl query type for Curation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCuration extends EntityPathBase<Curation> {

    private static final long serialVersionUID = -26553832L;

    public static final QCuration curation = new QCuration("curation");

    public final ListPath<CurationProductMap, QCurationProductMap> curationProductMaps = this.<CurationProductMap, QCurationProductMap>createList("curationProductMaps", CurationProductMap.class, QCurationProductMap.class, PathInits.DIRECT2);

    public final StringPath description = createString("description");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath image = createString("image");

    public final StringPath shortName = createString("shortName");

    public final NumberPath<Integer> sortNo = createNumber("sortNo", Integer.class);

    public final EnumPath<CurationState> state = createEnum("state", CurationState.class);

    public final StringPath title = createString("title");

    public final EnumPath<CurationType> type = createEnum("type", CurationType.class);

    public QCuration(String variable) {
        super(Curation.class, forVariable(variable));
    }

    public QCuration(Path<? extends Curation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCuration(PathMetadata metadata) {
        super(Curation.class, metadata);
    }

}

