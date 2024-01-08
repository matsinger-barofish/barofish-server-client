package com.matsinger.barofishserver.domain.grade.api;

import com.matsinger.barofishserver.domain.grade.application.GradeCommandService;
import com.matsinger.barofishserver.domain.grade.application.GradeQueryService;
import com.matsinger.barofishserver.domain.grade.domain.Grade;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/grade")
public class GradeController {

    private final GradeQueryService gradeQueryService;
    private final GradeCommandService gradeCommandService;
    private final JwtService jwt;
    private final Common utils;

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<Grade>>> selectGrade() {
        CustomResponse<List<Grade>> res = new CustomResponse<>();

        List<Grade> grades = gradeQueryService.selectGradeList();
        res.setData(Optional.ofNullable(grades));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<Grade>> selectGrade(@PathVariable("id") Integer id) {
        CustomResponse<Grade> res = new CustomResponse<>();

        Grade grade = gradeQueryService.selectGrade(id);
        res.setData(Optional.ofNullable(grade));
        return ResponseEntity.ok(res);
    }

    @Getter
    @NoArgsConstructor
    private static class AddGradeReq {
        String name;
        Float pointRate;
        Integer minOrderPrice;
        Integer minOrderCount;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Grade>> addGrade(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                          @RequestPart(value = "data") AddGradeReq data) {
        CustomResponse<Grade> res = new CustomResponse<>();

        if (data.name == null) throw new BusinessException("이름을 입력해주세요.");
        if (data.pointRate == null) throw new BusinessException("포인트 적립율을 입력해주세요.");
        if (data.minOrderPrice == null) throw new BusinessException("최소 주문 금액을 입력해주세요.");
        if (data.minOrderCount == null) throw new BusinessException("최소 주문 수량을 입력해주세요.");
        String name = utils.validateString(data.name, 20L, "이름");
        Grade
                grade =
                Grade.builder().name(name).pointRate(data.pointRate).minOrderPrice(data.minOrderPrice).minOrderCount(
                        data.minOrderCount).build();
        grade = gradeCommandService.addGrade(grade);
        res.setData(Optional.ofNullable(grade));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Grade>> updateGrade(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                             @PathVariable("id") Integer id,
                                                             @RequestPart(value = "data") AddGradeReq data) {
        CustomResponse<Grade> res = new CustomResponse<>();

                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

        Grade grade = gradeQueryService.selectGrade(id);
        if (data.name != null) {
            String name = utils.validateString(data.name, 20L, "이름");
            grade.setName(name);
        }
        if (data.pointRate != null) grade.setPointRate(data.pointRate);
        if (data.minOrderPrice != null) grade.setMinOrderPrice(data.minOrderPrice);
        if (data.minOrderCount != null) grade.setMinOrderCount(data.minOrderCount);

        grade = gradeCommandService.updateGrade(grade);
        res.setData(Optional.ofNullable(grade));
        return ResponseEntity.ok(res);
    }

}
