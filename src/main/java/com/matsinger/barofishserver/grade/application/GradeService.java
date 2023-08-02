package com.matsinger.barofishserver.grade.application;

import com.matsinger.barofishserver.grade.domain.Grade;
import com.matsinger.barofishserver.grade.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class GradeService {
    private final GradeRepository gradeRepository;




}
