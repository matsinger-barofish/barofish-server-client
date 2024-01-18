package com.matsinger.barofishserver.domain.store.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStoreInfo is a Querydsl query type for StoreInfo
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoreInfo extends EntityPathBase<StoreInfo> {

    private static final long serialVersionUID = 1616497860L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStoreInfo storeInfo = new QStoreInfo("storeInfo");

    public final StringPath addressDetail = createString("addressDetail");

    public final StringPath backgroundImage = createString("backgroundImage");

    public final StringPath bankAccount = createString("bankAccount");

    public final StringPath bankAccountCopy = createString("bankAccountCopy");

    public final StringPath bankHolder = createString("bankHolder");

    public final StringPath bankName = createString("bankName");

    public final StringPath businessAddress = createString("businessAddress");

    public final StringPath businessRegistration = createString("businessRegistration");

    public final StringPath businessType = createString("businessType");

    public final StringPath companyId = createString("companyId");

    public final StringPath deliverCompany = createString("deliverCompany");

    public final NumberPath<Integer> deliveryFee = createNumber("deliveryFee", Integer.class);

    public final StringPath email = createString("email");

    public final StringPath faxNumber = createString("faxNumber");

    public final BooleanPath isConditional = createBoolean("isConditional");

    public final BooleanPath isReliable = createBoolean("isReliable");

    public final StringPath keyword = createString("keyword");

    public final StringPath location = createString("location");

    public final StringPath lotNumberAddress = createString("lotNumberAddress");

    public final NumberPath<Integer> minStorePrice = createNumber("minStorePrice", Integer.class);

    public final StringPath mosRegistration = createString("mosRegistration");

    public final StringPath mosRegistrationNumber = createString("mosRegistrationNumber");

    public final StringPath name = createString("name");

    public final StringPath oneLineDescription = createString("oneLineDescription");

    public final StringPath postalCode = createString("postalCode");

    public final StringPath profileImage = createString("profileImage");

    public final NumberPath<Integer> refundDeliverFee = createNumber("refundDeliverFee", Integer.class);

    public final StringPath representativeName = createString("representativeName");

    public final NumberPath<Float> settlementRate = createNumber("settlementRate", Float.class);

    public final QStore store;

    public final NumberPath<Integer> storeId = createNumber("storeId", Integer.class);

    public final StringPath streetNameAddress = createString("streetNameAddress");

    public final StringPath tel = createString("tel");

    public final StringPath visitNote = createString("visitNote");

    public QStoreInfo(String variable) {
        this(StoreInfo.class, forVariable(variable), INITS);
    }

    public QStoreInfo(Path<? extends StoreInfo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStoreInfo(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStoreInfo(PathMetadata metadata, PathInits inits) {
        this(StoreInfo.class, metadata, inits);
    }

    public QStoreInfo(Class<? extends StoreInfo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new QStore(forProperty("store"), inits.get("store")) : null;
    }

}

