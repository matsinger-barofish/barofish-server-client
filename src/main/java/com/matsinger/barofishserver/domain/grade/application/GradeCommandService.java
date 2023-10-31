package com.matsinger.barofishserver.domain.grade.application;

import com.matsinger.barofishserver.domain.grade.domain.Grade;
import com.matsinger.barofishserver.domain.grade.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class GradeCommandService {
    private final GradeRepository gradeRepository;

    public Grade addGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    public Grade updateGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    public void deleteGrade(Integer id) {
        gradeRepository.deleteById(id);
    }
}
