package com.matsinger.barofishserver.domain.product.weeksdate.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWeeksDate is a Querydsl query type for WeeksDate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWeeksDate extends EntityPathBase<WeeksDate> {

    private static final long serialVersionUID = 554786677L;

    public static final QWeeksDate weeksDate = new QWeeksDate("weeksDate");

    public final StringPath date = createString("date");

    public final StringPath description = createString("description");

    public final BooleanPath isDeliveryCompanyHoliday = createBoolean("isDeliveryCompanyHoliday");

    public QWeeksDate(String variable) {
        super(WeeksDate.class, forVariable(variable));
    }

    public QWeeksDate(Path<? extends WeeksDate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWeeksDate(PathMetadata metadata) {
        super(WeeksDate.class, metadata);
    }

}

