package com.matsinger.barofishserver.user;

import com.matsinger.barofishserver.user.object.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade,Integer> {
}
