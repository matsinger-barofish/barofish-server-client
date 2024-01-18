package com.matsinger.barofishserver.domain.compare.recommend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRecommendCompareSet is a Querydsl query type for RecommendCompareSet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecommendCompareSet extends EntityPathBase<RecommendCompareSet> {

    private static final long serialVersionUID = -320173028L;

    public static final QRecommendCompareSet recommendCompareSet = new QRecommendCompareSet("recommendCompareSet");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> product1Id = createNumber("product1Id", Integer.class);

    public final NumberPath<Integer> product2Id = createNumber("product2Id", Integer.class);

    public final NumberPath<Integer> product3Id = createNumber("product3Id", Integer.class);

    public final EnumPath<RecommendCompareSetType> type = createEnum("type", RecommendCompareSetType.class);

    public QRecommendCompareSet(String variable) {
        super(RecommendCompareSet.class, forVariable(variable));
    }

    public QRecommendCompareSet(Path<? extends RecommendCompareSet> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRecommendCompareSet(PathMetadata metadata) {
        super(RecommendCompareSet.class, metadata);
    }

}

