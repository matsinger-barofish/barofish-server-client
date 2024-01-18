package com.matsinger.barofishserver.domain.productinfonotice.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProcessedFoodInfo is a Querydsl query type for ProcessedFoodInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProcessedFoodInfo extends EntityPathBase<ProcessedFoodInfo> {

    private static final long serialVersionUID = 1326852955L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProcessedFoodInfo processedFoodInfo = new QProcessedFoodInfo("processedFoodInfo");

    public final StringPath cautionGuidelines = createString("cautionGuidelines");

    public final StringPath geneticallyModifiedInfo = createString("geneticallyModifiedInfo");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath importedPhrase = createString("importedPhrase");

    public final StringPath nameOfProduct = createString("nameOfProduct");

    public final StringPath nutritionalIngredients = createString("nutritionalIngredients");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath producer = createString("producer");

    public final com.matsinger.barofishserver.domain.product.domain.QProduct product;

    public final StringPath qualityMaintenanceDeadline = createString("qualityMaintenanceDeadline");

    public final StringPath rawMaterialInfo = createString("rawMaterialInfo");

    public final StringPath typesOfFood = createString("typesOfFood");

    public final StringPath volume = createString("volume");

    public QProcessedFoodInfo(String variable) {
        this(ProcessedFoodInfo.class, forVariable(variable), INITS);
    }

    public QProcessedFoodInfo(Path<? extends ProcessedFoodInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProcessedFoodInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProcessedFoodInfo(PathMetadata metadata, PathInits inits) {
        this(ProcessedFoodInfo.class, metadata, inits);
    }

    public QProcessedFoodInfo(Class<? extends ProcessedFoodInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.product = inits.isInitialized("product") ? new com.matsinger.barofishserver.domain.product.domain.QProduct(forProperty("product"), inits.get("product")) : null;
    }

}

