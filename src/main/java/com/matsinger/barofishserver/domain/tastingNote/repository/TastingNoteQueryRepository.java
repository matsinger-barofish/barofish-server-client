package com.matsinger.barofishserver.domain.tastingNote.repository;

import com.matsinger.barofishserver.domain.tastingNote.dto.ProductTastingNoteResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.matsinger.barofishserver.domain.tastingNote.domain.QTastingNote.tastingNote;

@Repository
@RequiredArgsConstructor
public class TastingNoteQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ProductTastingNoteResponse getTastingNotesScore(Integer productId) {
        return queryFactory.select(Projections.fields(
                ProductTastingNoteResponse.class,
                tastingNote.productId.as("productId"),
                tastingNote.oily.avg().as("taste1Sum"),
                tastingNote.taste2.avg().as("taste2Sum"),
                tastingNote.taste3.avg().as("taste3Sum"),
                tastingNote.taste4.avg().as("taste4Sum"),
                tastingNote.taste5.avg().as("taste5Sum"),
                tastingNote.tendernessSoftness.avg().as("texture1Sum"),
                tastingNote.texture2.avg().as("texture2Sum"),
                tastingNote.texture3.avg().as("texture3Sum"),
                tastingNote.texture4.avg().as("texture4Sum"),
                tastingNote.texture5.avg().as("texture5Sum")
                ))
                .from(tastingNote)
                .where(tastingNote.productId.eq(productId))
                .fetchOne();
    }
}
