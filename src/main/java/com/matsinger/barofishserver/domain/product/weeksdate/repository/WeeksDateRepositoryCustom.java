package com.matsinger.barofishserver.domain.product.weeksdate.repository;

import com.matsinger.barofishserver.domain.product.weeksdate.domain.WeeksDate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeeksDateRepositoryCustom {
    List<WeeksDate> getWeeksDate(LocalDate startDate, LocalDate endDate);
}
