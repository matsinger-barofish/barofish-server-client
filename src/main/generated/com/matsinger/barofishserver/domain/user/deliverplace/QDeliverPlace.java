package com.matsinger.barofishserver.domain.user.deliverplace;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDeliverPlace is a Querydsl query type for DeliverPlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDeliverPlace extends EntityPathBase<DeliverPlace> {

    private static final long serialVersionUID = -576693507L;

    public static final QDeliverPlace deliverPlace = new QDeliverPlace("deliverPlace");

    public final StringPath address = createString("address");

    public final StringPath addressDetail = createString("addressDetail");

    public final StringPath bcode = createString("bcode");

    public final StringPath deliverMessage = createString("deliverMessage");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final BooleanPath isDefault = createBoolean("isDefault");

    public final StringPath name = createString("name");

    public final StringPath postalCode = createString("postalCode");

    public final StringPath receiverName = createString("receiverName");

    public final StringPath tel = createString("tel");

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public QDeliverPlace(String variable) {
        super(DeliverPlace.class, forVariable(variable));
    }

    public QDeliverPlace(Path<? extends DeliverPlace> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDeliverPlace(PathMetadata metadata) {
        super(DeliverPlace.class, metadata);
    }

}

