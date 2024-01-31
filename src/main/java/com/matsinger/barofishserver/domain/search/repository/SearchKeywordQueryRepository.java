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
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.matsinger.barofishserver.domain.product.domain.QProduct.product;
import static com.matsinger.barofishserver.domain.store.domain.QStore.store;
import static com.matsinger.barofishserver.domain.store.domain.QStoreInfo.storeInfo;

@Repository
@RequiredArgsConstructor
public class SearchKeywordQueryRepository {

    private final JPAQueryFactory queryFactory;


    public List<SearchProductDto> selectSearchKeyword(String keyword) {
        String convertedKeyword = keyword.replace("\\s+", " "); // 여러개의 공백을 공백 하나로
        String[] keywords = convertedKeyword.split(" ");
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
                .where(storeInfo.name.like(convertedKeyword)
                        .or(containsAll(product.title, keywords))
                        .and(product.state.eq(ProductState.ACTIVE))
                        .and(store.state.eq(StoreState.ACTIVE)))
                .fetch();
    }

    private BooleanExpression containsAll(StringPath title, String[] keywords) {
        BooleanExpression allMatches = null;
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
