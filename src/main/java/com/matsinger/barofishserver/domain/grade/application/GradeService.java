package com.matsinger.barofishserver.domain.grade.application;

import com.matsinger.barofishserver.domain.grade.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class GradeService {
    private final GradeRepository gradeRepository;




}
