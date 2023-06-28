package com.matsinger.barofishserver.grade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class GradeService {
    private final GradeRepository gradeRepository;

    public List<Grade> selectGradeList() {
        return gradeRepository.findAll();
    }

    public Grade selectGrade(Integer id) {
        return gradeRepository.findById(id).orElseThrow(() -> {
            throw new Error("등급 정보를 찾을 수 없습니다.");
        });
    }

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
