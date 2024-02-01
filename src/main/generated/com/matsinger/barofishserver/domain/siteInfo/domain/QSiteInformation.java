package com.matsinger.barofishserver.domain.siteInfo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSiteInformation is a Querydsl query type for SiteInformation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSiteInformation extends EntityPathBase<SiteInformation> {

    private static final long serialVersionUID = -1145258782L;

    public static final QSiteInformation siteInformation = new QSiteInformation("siteInformation");

    public final StringPath content = createString("content");

    public final StringPath description = createString("description");

    public final StringPath id = createString("id");

    public QSiteInformation(String variable) {
        super(SiteInformation.class, forVariable(variable));
    }

    public QSiteInformation(Path<? extends SiteInformation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSiteInformation(PathMetadata metadata) {
        super(SiteInformation.class, metadata);
    }

}

