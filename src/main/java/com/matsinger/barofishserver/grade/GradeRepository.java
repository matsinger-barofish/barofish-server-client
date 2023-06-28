package com.matsinger.barofishserver.grade;

import com.matsinger.barofishserver.grade.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade,Integer> {
}
