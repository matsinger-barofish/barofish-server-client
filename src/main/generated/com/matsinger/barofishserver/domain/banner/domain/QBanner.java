package com.matsinger.barofishserver.domain.banner.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBanner is a Querydsl query type for Banner
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBanner extends EntityPathBase<Banner> {

    private static final long serialVersionUID = -86675482L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBanner banner = new QBanner("banner");

    public final com.matsinger.barofishserver.domain.category.domain.QCategory category;

    public final NumberPath<Integer> categoryId = createNumber("categoryId", Integer.class);

    public final com.matsinger.barofishserver.domain.data.curation.domain.QCuration curation;

    public final NumberPath<Integer> curationId = createNumber("curationId", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath image = createString("image");

    public final StringPath link = createString("link");

    public final com.matsinger.barofishserver.domain.notice.domain.QNotice notice;

    public final NumberPath<Integer> noticeId = createNumber("noticeId", Integer.class);

    public final NumberPath<Integer> sortNo = createNumber("sortNo", Integer.class);

    public final EnumPath<BannerState> state = createEnum("state", BannerState.class);

    public final EnumPath<BannerType> type = createEnum("type", BannerType.class);

    public QBanner(String variable) {
        this(Banner.class, forVariable(variable), INITS);
    }

    public QBanner(Path<? extends Banner> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBanner(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBanner(PathMetadata metadata, PathInits inits) {
        this(Banner.class, metadata, inits);
    }

    public QBanner(Class<? extends Banner> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.matsinger.barofishserver.domain.category.domain.QCategory(forProperty("category"), inits.get("category")) : null;
        this.curation = inits.isInitialized("curation") ? new com.matsinger.barofishserver.domain.data.curation.domain.QCuration(forProperty("curation")) : null;
        this.notice = inits.isInitialized("notice") ? new com.matsinger.barofishserver.domain.notice.domain.QNotice(forProperty("notice")) : null;
    }

}

