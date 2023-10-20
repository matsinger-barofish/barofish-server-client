package com.matsinger.barofishserver.product.weeksdate.repository;

import com.matsinger.barofishserver.product.weeksdate.domain.WeeksDate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.matsinger.barofishserver.product.weeksdate.domain.QWeeksDate.weeksDate;

@Repository
@RequiredArgsConstructor
public class WeeksDateRepositoryImpl implements WeeksDateRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<WeeksDate> getWeeksDate(LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(weeksDate)
                .where(weeksDate.date.goe(startDate.toString().replace("-", ""))
                        .and(weeksDate.date.loe(endDate.toString().replace("-", ""))))
                .fetch();
    }
}
