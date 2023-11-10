package com.matsinger.barofishserver.domain.compare.filter.api;

import com.matsinger.barofishserver.domain.compare.filter.application.CompareFilterCommandService;
import com.matsinger.barofishserver.domain.compare.filter.application.CompareFilterQueryService;
import com.matsinger.barofishserver.domain.compare.filter.dto.CompareFilterDto;
import com.matsinger.barofishserver.domain.compare.filter.domain.CompareFilter;
import com.matsinger.barofishserver.global.error.ErrorCode;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.jwt.exception.JwtBusinessException;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/compare/filter")
public class CompareFilterController {

    private final CompareFilterQueryService compareFilterQueryService;
    private final CompareFilterCommandService compareFilterCommandService;
    private final JwtService jwt;
    private final Common utils;

    @GetMapping("/management")
    public ResponseEntity<CustomResponse<List<CompareFilter>>> selectCompareFilterAllList(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<CompareFilter>> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        List<CompareFilter> compareFilters = compareFilterQueryService.selectCompareFilterList();
        res.setData(Optional.ofNullable(compareFilters));
        return ResponseEntity.ok(res);
    }

    @Getter
    private static class AddCompareFilterReq {
        String name;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<CompareFilterDto>> addCompareFilter(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                             @RequestPart(value = "data") AddCompareFilterReq data) throws Exception {
        CustomResponse<CompareFilterDto> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        String name = utils.validateString(data.name, 20L, "이름");
        CompareFilter
                compareFilter =
                compareFilterCommandService.addCompareFilter(CompareFilter.builder().name(name).build());
        res.setData(Optional.ofNullable(compareFilter.convert2Dto()));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<CompareFilterDto>> addCompareFilter(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                             @PathVariable("id") Integer id,
                                                                             @RequestPart(value = "data") AddCompareFilterReq data) throws Exception {
        CustomResponse<CompareFilterDto> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        CompareFilter compareFilter = compareFilterQueryService.selectCompareFilter(id);
        if (data.name != null) {
            String name = utils.validateString(data.name, 20L, "이름");
            compareFilter.setName(name);
        }
        compareFilter = compareFilterCommandService.updateCompareFilter(compareFilter);
        res.setData(Optional.ofNullable(compareFilter.convert2Dto()));
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> addCompareFilter(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                    @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        compareFilterQueryService.selectCompareFilter(id);
        compareFilterCommandService.deleteCompareFilter(id);
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }
}
