package com.matsinger.barofishserver.domain.product.optionitem.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOptionItem is a Querydsl query type for OptionItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOptionItem extends EntityPathBase<OptionItem> {

    private static final long serialVersionUID = -1799152291L;

    public static final QOptionItem optionItem = new QOptionItem("optionItem");

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    public final NumberPath<Integer> deliverBoxPerAmount = createNumber("deliverBoxPerAmount", Integer.class);

    public final NumberPath<Integer> deliverFee = createNumber("deliverFee", Integer.class);

    public final NumberPath<Integer> discountPrice = createNumber("discountPrice", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> maxAvailableAmount = createNumber("maxAvailableAmount", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> optionId = createNumber("optionId", Integer.class);

    public final NumberPath<Integer> originPrice = createNumber("originPrice", Integer.class);

    public final NumberPath<Integer> purchasePrice = createNumber("purchasePrice", Integer.class);

    public final EnumPath<com.matsinger.barofishserver.domain.product.domain.OptionItemState> state = createEnum("state", com.matsinger.barofishserver.domain.product.domain.OptionItemState.class);

    public QOptionItem(String variable) {
        super(OptionItem.class, forVariable(variable));
    }

    public QOptionItem(Path<? extends OptionItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOptionItem(PathMetadata metadata) {
        super(OptionItem.class, metadata);
    }

}

