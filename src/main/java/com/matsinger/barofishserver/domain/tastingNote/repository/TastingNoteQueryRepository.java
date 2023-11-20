package com.matsinger.barofishserver.domain.tastingNote.repository;

import com.matsinger.barofishserver.domain.tastingNote.dto.ProductTastingNoteInquiryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.matsinger.barofishserver.domain.product.domain.QProduct.product;
import static com.matsinger.barofishserver.domain.tastingNote.domain.QTastingNote.tastingNote;

@Repository
@RequiredArgsConstructor
public class TastingNoteQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ProductTastingNoteInquiryDto getTastingNotesScore(Integer productId) {
        return queryFactory.select(Projections.fields(
                ProductTastingNoteInquiryDto.class,
                product.id.as("productId"),
                Expressions.asString("oily").as("taste1"),
                tastingNote.oily.avg().as("taste1Score"),
                Expressions.asString("taste2").as("taste2"),
                tastingNote.taste2.avg().as("taste2Score"),
                Expressions.asString("taste3").as("taste3"),
                tastingNote.taste3.avg().as("taste3Score"),
                Expressions.asString("taste4").as("taste4"),
                tastingNote.taste4.avg().as("taste4Score"),
                Expressions.asString("taste5").as("taste5"),
                tastingNote.taste5.avg().as("taste5Score"),
                Expressions.asString("tendernessSoftness").as("texture1"),
                tastingNote.tendernessSoftness.avg().as("texture1Score"),
                Expressions.asString("texture2").as("texture2"),
                tastingNote.texture2.avg().as("texture2Score"),
                Expressions.asString("texture3").as("texture3"),
                tastingNote.texture3.avg().as("texture3Score"),
                Expressions.asString("texture4").as("texture4"),
                tastingNote.texture4.avg().as("texture4Score"),
                Expressions.asString("texture5").as("texture5"),
                tastingNote.texture5.avg().as("texture5Score"),
                product.difficultyLevelOfTrimming.as("difficultyLevelOfTrimming"),
                product.theScentOfTheSea.as("theScentOfTheSea"),
                product.recommendedCookingWay.as("recommendedCookingWay")
                ))
                .from(product)
                .leftJoin(tastingNote).on(product.id.eq(tastingNote.productId))
                .where(product.id.eq(productId))
                .fetchOne();
    }
}
