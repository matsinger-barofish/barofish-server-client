package com.matsinger.barofishserver.domain.basketProduct.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBasketProductOption is a Querydsl query type for BasketProductOption
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBasketProductOption extends EntityPathBase<BasketProductOption> {

    private static final long serialVersionUID = -772342517L;

    public static final QBasketProductOption basketProductOption = new QBasketProductOption("basketProductOption");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> optionId = createNumber("optionId", Integer.class);

    public final NumberPath<Integer> orderProductId = createNumber("orderProductId", Integer.class);

    public QBasketProductOption(String variable) {
        super(BasketProductOption.class, forVariable(variable));
    }

    public QBasketProductOption(Path<? extends BasketProductOption> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBasketProductOption(PathMetadata metadata) {
        super(BasketProductOption.class, metadata);
    }

}

