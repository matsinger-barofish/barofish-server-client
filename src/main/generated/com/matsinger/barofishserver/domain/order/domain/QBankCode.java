package com.matsinger.barofishserver.domain.order.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBankCode is a Querydsl query type for BankCode
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBankCode extends EntityPathBase<BankCode> {

    private static final long serialVersionUID = 130950273L;

    public static final QBankCode bankCode = new QBankCode("bankCode");

    public final StringPath code = createString("code");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public QBankCode(String variable) {
        super(BankCode.class, forVariable(variable));
    }

    public QBankCode(Path<? extends BankCode> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBankCode(PathMetadata metadata) {
        super(BankCode.class, metadata);
    }

}

