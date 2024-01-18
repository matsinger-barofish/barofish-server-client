package com.matsinger.barofishserver.domain.basketProduct.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBasketProductInfo is a Querydsl query type for BasketProductInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBasketProductInfo extends EntityPathBase<BasketProductInfo> {

    private static final long serialVersionUID = -1895954748L;

    public static final QBasketProductInfo basketProductInfo = new QBasketProductInfo("basketProductInfo");

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    public final NumberPath<Integer> deliveryFee = createNumber("deliveryFee", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final BooleanPath isNeeded = createBoolean("isNeeded");

    public final NumberPath<Integer> optionId = createNumber("optionId", Integer.class);

    public final NumberPath<Integer> optionItemId = createNumber("optionItemId", Integer.class);

    public final NumberPath<Integer> productId = createNumber("productId", Integer.class);

    public final NumberPath<Integer> storeId = createNumber("storeId", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QBasketProductInfo(String variable) {
        super(BasketProductInfo.class, forVariable(variable));
    }

    public QBasketProductInfo(Path<? extends BasketProductInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBasketProductInfo(PathMetadata metadata) {
        super(BasketProductInfo.class, metadata);
    }

}

