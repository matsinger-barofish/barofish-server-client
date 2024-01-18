package com.matsinger.barofishserver.domain.product.option.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOption is a Querydsl query type for Option
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOption extends EntityPathBase<Option> {

    private static final long serialVersionUID = 733644887L;

    public static final QOption option = new QOption("option");

    public final StringPath description = createString("description");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final BooleanPath isNeeded = createBoolean("isNeeded");

    public final NumberPath<Integer> productId = createNumber("productId", Integer.class);

    public final EnumPath<com.matsinger.barofishserver.domain.product.domain.OptionState> state = createEnum("state", com.matsinger.barofishserver.domain.product.domain.OptionState.class);

    public QOption(String variable) {
        super(Option.class, forVariable(variable));
    }

    public QOption(Path<? extends Option> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOption(PathMetadata metadata) {
        super(Option.class, metadata);
    }

}

