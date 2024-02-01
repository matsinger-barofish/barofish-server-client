package com.matsinger.barofishserver.domain.product.weeksdate.repository;

import com.matsinger.barofishserver.domain.product.weeksdate.domain.WeeksDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeksDateRepository extends JpaRepository<WeeksDate, String> {
    WeeksDate findByDate(String date);

    List<WeeksDate> findByDateBetween(String startDate, String endDate);

}
