package com.matsinger.barofishserver.domain.address.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAddress is a Querydsl query type for Address
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAddress extends EntityPathBase<Address> {

    private static final long serialVersionUID = -353750826L;

    public static final QAddress address = new QAddress("address");

    public final StringPath bcode = createString("bcode");

    public final StringPath bname = createString("bname");

    public final StringPath hcode = createString("hcode");

    public final StringPath hname = createString("hname");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath sido = createString("sido");

    public final StringPath sigungu = createString("sigungu");

    public QAddress(String variable) {
        super(Address.class, forVariable(variable));
    }

    public QAddress(Path<? extends Address> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAddress(PathMetadata metadata) {
        super(Address.class, metadata);
    }

}

