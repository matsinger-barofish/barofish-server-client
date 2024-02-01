package com.matsinger.barofishserver.domain.search.repository;

import com.matsinger.barofishserver.domain.product.domain.ProductState;
import com.matsinger.barofishserver.domain.search.dto.SearchProductDto;
import com.matsinger.barofishserver.domain.store.domain.StoreState;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.matsinger.barofishserver.domain.product.domain.QProduct.product;
import static com.matsinger.barofishserver.domain.store.domain.QStore.store;
import static com.matsinger.barofishserver.domain.store.domain.QStoreInfo.storeInfo;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SearchKeywordQueryRepository {

    private final JPAQueryFactory queryFactory;


    public List<SearchProductDto> selectSearchKeyword(String[] keywords) {

        return queryFactory
                .select(Projections.fields(
                        SearchProductDto.class,
                        product.id.as("id"),
                        Expressions.asString("[").concat(
                                storeInfo.name).concat("] ")
                                .concat(product.title)
                                .as("title")
                ))
                .from(product)
                .leftJoin(storeInfo).on(product.storeId.eq(storeInfo.storeId))
                .leftJoin(store).on(store.id.eq(storeInfo.storeId))
                .where(matches(storeInfo.name, keywords)
                        .or(contains(product.title, keywords))
                        .and(product.state.eq(ProductState.ACTIVE))
                        .and(store.state.eq(StoreState.ACTIVE)))
                .fetch();
    }

    private BooleanExpression matches(StringPath storeName, String[] keywords) {
        BooleanExpression keywordMatchesStoreName = null;
        for (String keyword : keywords) {
            if (keywordMatchesStoreName == null) {
                keywordMatchesStoreName = storeName.contains(keyword);
            } else {
                keywordMatchesStoreName.or(storeName.contains(keyword));
            }
        }
        return keywordMatchesStoreName;
    }

    private BooleanExpression contains(StringPath title, String[] keywords) {
        BooleanExpression allMatches = null;
        int matchingWordCnt = 0;
        for (String keyword : keywords) {
            if (allMatches == null) {
                allMatches = title.contains(keyword);
            } else {
                allMatches.and(title.contains(keyword));
            }
        }
        return allMatches;
    }
}
