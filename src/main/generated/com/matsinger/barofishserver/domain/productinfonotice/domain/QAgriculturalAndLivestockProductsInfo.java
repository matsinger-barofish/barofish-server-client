package com.matsinger.barofishserver.domain.productinfonotice.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAgriculturalAndLivestockProductsInfo is a Querydsl query type for AgriculturalAndLivestockProductsInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAgriculturalAndLivestockProductsInfo extends EntityPathBase<AgriculturalAndLivestockProductsInfo> {

    private static final long serialVersionUID = -977512701L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAgriculturalAndLivestockProductsInfo agriculturalAndLivestockProductsInfo = new QAgriculturalAndLivestockProductsInfo("agriculturalAndLivestockProductsInfo");

    public final StringPath cautionGuidelines = createString("cautionGuidelines");

    public final StringPath contentsOfProduct = createString("contentsOfProduct");

    public final StringPath geneticallyModifiedInfo = createString("geneticallyModifiedInfo");

    public final StringPath howToKeep = createString("howToKeep");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath importInformation = createString("importInformation");

    public final StringPath nameOfProduct = createString("nameOfProduct");

    public final StringPath originCountry = createString("originCountry");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath producer = createString("producer");

    public final com.matsinger.barofishserver.domain.product.domain.QProduct product;

    public final StringPath productGrade = createString("productGrade");

    public final StringPath qualityMaintenanceDeadline = createString("qualityMaintenanceDeadline");

    public final StringPath volume = createString("volume");

    public QAgriculturalAndLivestockProductsInfo(String variable) {
        this(AgriculturalAndLivestockProductsInfo.class, forVariable(variable), INITS);
    }

    public QAgriculturalAndLivestockProductsInfo(Path<? extends AgriculturalAndLivestockProductsInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAgriculturalAndLivestockProductsInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAgriculturalAndLivestockProductsInfo(PathMetadata metadata, PathInits inits) {
        this(AgriculturalAndLivestockProductsInfo.class, metadata, inits);
    }

    public QAgriculturalAndLivestockProductsInfo(Class<? extends AgriculturalAndLivestockProductsInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new com.matsinger.barofishserver.domain.product.domain.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

