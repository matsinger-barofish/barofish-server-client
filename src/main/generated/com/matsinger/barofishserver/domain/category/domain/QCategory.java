package com.matsinger.barofishserver.domain.category.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCategory is a Querydsl query type for Category
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCategory extends EntityPathBase<Category> {

    private static final long serialVersionUID = -1718694774L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCategory category = new QCategory("category");

    public final NumberPath<Integer> categoryId = createNumber("categoryId", Integer.class);

    public final ListPath<Category, QCategory> categoryList = this.<Category, QCategory>createList("categoryList", Category.class, QCategory.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath image = createString("image");

    public final StringPath name = createString("name");

    public final QCategory parentCategory;

    public QCategory(String variable) {
        this(Category.class, forVariable(variable), INITS);
    }

    public QCategory(Path<? extends Category> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCategory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCategory(PathMetadata metadata, PathInits inits) {
        this(Category.class, metadata, inits);
    }

    public QCategory(Class<? extends Category> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.parentCategory = inits.isInitialized("parentCategory") ? new QCategory(forProperty("parentCategory"), inits.get("parentCategory")) : null;
    }

}

