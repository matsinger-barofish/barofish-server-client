package com.matsinger.barofishserver.domain.order.orderprductinfo.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QOrderProductOption is a Querydsl query type for OrderProductOption
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrderProductOption extends EntityPathBase<OrderProductOption> {

    private static final long serialVersionUID = -230082656L;

    public static final QOrderProductOption orderProductOption = new QOrderProductOption("orderProductOption");

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> orderProductId = createNumber("orderProductId", Integer.class);

    public final NumberPath<Integer> price = createNumber("price", Integer.class);

    public QOrderProductOption(String variable) {
        super(OrderProductOption.class, forVariable(variable));
    }

    public QOrderProductOption(Path<? extends OrderProductOption> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOrderProductOption(PathMetadata metadata) {
        super(OrderProductOption.class, metadata);
    }

}

