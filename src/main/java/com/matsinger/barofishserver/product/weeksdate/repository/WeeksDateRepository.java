package com.matsinger.barofishserver.product.weeksdate.repository;

import com.matsinger.barofishserver.product.weeksdate.domain.WeeksDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WeeksDateRepository extends JpaRepository<WeeksDate, String> {

    List<WeeksDate> findDatesBetweenStartDateAndEndDate(LocalDate startDate, LocalDate endDate);
}
