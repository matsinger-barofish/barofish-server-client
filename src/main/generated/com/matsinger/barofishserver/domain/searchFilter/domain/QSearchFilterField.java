package com.matsinger.barofishserver.domain.searchFilter.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSearchFilterField is a Querydsl query type for SearchFilterField
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSearchFilterField extends EntityPathBase<SearchFilterField> {

    private static final long serialVersionUID = 2075802764L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSearchFilterField searchFilterField = new QSearchFilterField("searchFilterField");

    public final StringPath field = createString("field");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QSearchFilter searchFilter;

    public final NumberPath<Integer> searchFilterId = createNumber("searchFilterId", Integer.class);

    public QSearchFilterField(String variable) {
        this(SearchFilterField.class, forVariable(variable), INITS);
    }

    public QSearchFilterField(Path<? extends SearchFilterField> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSearchFilterField(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSearchFilterField(PathMetadata metadata, PathInits inits) {
        this(SearchFilterField.class, metadata, inits);
    }

    public QSearchFilterField(Class<? extends SearchFilterField> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.searchFilter = inits.isInitialized("searchFilter") ? new QSearchFilter(forProperty("searchFilter")) : null;
    }

}

