package com.matsinger.barofishserver.compare.filter;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.compare.CompareItemService;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
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
@RequestMapping("/api/v1/compare/filter")
public class CompareFilterController {

    private final CompareFilterService compareFilterService;
    private final JwtService jwt;
    private final Common utils;

    @GetMapping("/management")
    public ResponseEntity<CustomResponse<List<CompareFilter>>> selectCompareFilterAllList(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<CompareFilter>> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<CompareFilter> compareFilters = compareFilterService.selectCompareFilterList();
            res.setData(Optional.ofNullable(compareFilters));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    private static class AddCompareFilterReq {
        String name;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<CompareFilterDto>> addCompareFilter(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                             @RequestPart(value = "data") AddCompareFilterReq data) {
        CustomResponse<CompareFilterDto> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            String name = utils.validateString(data.name, 20L, "이름");
            CompareFilter
                    compareFilter =
                    compareFilterService.addCompareFilter(CompareFilter.builder().name(name).build());
            res.setData(Optional.ofNullable(compareFilter.convert2Dto()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<CompareFilterDto>> addCompareFilter(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                             @PathVariable("id") Integer id,
                                                                             @RequestPart(value = "data") AddCompareFilterReq data) {
        CustomResponse<CompareFilterDto> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            CompareFilter compareFilter = compareFilterService.selectCompareFilter(id);
            if (data.name != null) {
                String name = utils.validateString(data.name, 20L, "이름");
                compareFilter.setName(name);
            }
            compareFilter = compareFilterService.updateCompareFilter(compareFilter);
            res.setData(Optional.ofNullable(compareFilter.convert2Dto()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> addCompareFilter(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                    @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            compareFilterService.selectCompareFilter(id);
            compareFilterService.deleteCompareFilter(id);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
