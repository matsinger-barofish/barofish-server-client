package com.matsinger.barofishserver.grade.repository;

import com.matsinger.barofishserver.grade.domain.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
}
