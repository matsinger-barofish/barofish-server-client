package com.matsinger.barofishserver.domain.searchFilter.api;

import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.searchFilter.domain.SearchFilter;
import com.matsinger.barofishserver.domain.searchFilter.domain.SearchFilterField;
import com.matsinger.barofishserver.domain.searchFilter.dto.SearchFilterFieldDto;
import com.matsinger.barofishserver.global.error.ErrorCode;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.searchFilter.application.SearchFilterCommandService;
import com.matsinger.barofishserver.domain.searchFilter.application.SearchFilterQueryService;
import com.matsinger.barofishserver.domain.searchFilter.dto.SearchFilterDto;
import com.matsinger.barofishserver.jwt.exception.JwtBusinessException;
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
@RequestMapping("/api/v1/search-filter")
public class SearchFilterController {

    private final SearchFilterQueryService searchFilterQueryService;
    private final SearchFilterCommandService searchFilterCommandService;
    private final ProductService productService;
    private final JwtService jwt;
    private final Common utils;

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<SearchFilterDto>>> selectSearchFilterList() {
        CustomResponse<List<SearchFilterDto>> res = new CustomResponse<>();

        List<SearchFilterDto>
                searchFilterDtos =
                searchFilterQueryService.selectSearchFilterList().stream().map(searchFilterCommandService::convertFilterDto).toList();
        res.setData(Optional.of(searchFilterDtos));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<SearchFilterDto>> selectSearchFilter(@PathVariable("id") Integer id) {
        CustomResponse<SearchFilterDto> res = new CustomResponse<>();

        if (id == null) throw new IllegalArgumentException("검색 필터 아이디를 입력해주세요.");
        SearchFilterDto
                searchFilterDto =
                searchFilterCommandService.convertFilterDto(searchFilterQueryService.selectSearchFilter(id));
        res.setData(Optional.ofNullable(searchFilterDto));
        return ResponseEntity.ok(res);
    }

    @Getter
    @NoArgsConstructor
    private static class AddSearchFilterReq {
        String name;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<SearchFilterDto>> addSearchFilter(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                           @RequestPart(value = "data") AddSearchFilterReq data) throws Exception {
        CustomResponse<SearchFilterDto> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        String name = utils.validateString(data.name, 20L, "이름");
        SearchFilter searchFilter = SearchFilter.builder().name(name).build();
        SearchFilterDto
                searchFilterDto =
                searchFilterCommandService.convertFilterDto(searchFilterCommandService.addSearchFilter(searchFilter));
        res.setData(Optional.ofNullable(searchFilterDto));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<SearchFilterDto>> addSearchFilter(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                           @PathVariable("id") Integer id,
                                                                           @RequestPart(value = "data") AddSearchFilterReq data) throws Exception {
        CustomResponse<SearchFilterDto> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        SearchFilter searchFilter = searchFilterQueryService.selectSearchFilter(id);
        if (data.name != null) {
            String name = utils.validateString(data.name, 20L, "이름");
            searchFilter.setName(name);
        }
        SearchFilterDto
                searchFilterDto =
                searchFilterCommandService.convertFilterDto(searchFilterCommandService.updateSearchFilter(
                        searchFilter));
        res.setData(Optional.ofNullable(searchFilterDto));
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteSearchFilter(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        SearchFilter searchFilter = searchFilterQueryService.selectSearchFilter(id);
        searchFilterCommandService.deleteSearchFilter(id);
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }

    // SearchFilterField
    @GetMapping("/{filterId}/list")
    public ResponseEntity<CustomResponse<List<SearchFilterFieldDto>>> selectSearchFilterFieldList(@PathVariable("filterId") Integer filterId) {
        CustomResponse<List<SearchFilterFieldDto>> res = new CustomResponse<>();

        List<SearchFilterField>
                searchFilterFields =
                searchFilterQueryService.selectSearchFilterFieldWithFilterId(filterId);
        res.setData(Optional.of(searchFilterFields.stream().map(SearchFilterField::convert2Dto).toList()));
        return ResponseEntity.ok(res);
    }

    @Getter
    @NoArgsConstructor
    private static class AddSearchFilterFiledReq {
        Integer searchFilterId;
        String field;
    }

    @PostMapping("/field/add")
    public ResponseEntity<CustomResponse<SearchFilterFieldDto>> addSearchFilterField(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                     @RequestPart(value = "data") AddSearchFilterFiledReq data) throws Exception {
        CustomResponse<SearchFilterFieldDto> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        if (data.searchFilterId == null) throw new IllegalArgumentException("필터 아이디를 입력해주세요.");
        if (data.field == null) throw new IllegalArgumentException("필드를 입력해주세요.");
        String field = utils.validateString(data.field, 20L, "필드명");
        SearchFilterField
                searchFilterField =
                SearchFilterField.builder().searchFilterId(data.searchFilterId).field(field).build();
        SearchFilterFieldDto
                searchFilterFieldDto =
                searchFilterCommandService.addSearchFilterField(searchFilterField).convert2Dto();
        res.setData(Optional.ofNullable(searchFilterFieldDto));
        return ResponseEntity.ok(res);
    }

    @Getter
    @NoArgsConstructor
    private static class UpdateSearchFilterFiledReq {
        String field;
    }

    @PostMapping("/field/update/{id}")
    public ResponseEntity<CustomResponse<SearchFilterFieldDto>> updateSearchFilterField(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                        @PathVariable("id") Integer id,
                                                                                        @RequestPart(value = "data") UpdateSearchFilterFiledReq data) throws Exception {
        CustomResponse<SearchFilterFieldDto> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        SearchFilterField searchFilterField = searchFilterQueryService.selectSearchFilterField(id);
        if (data.field != null) {
            String field = utils.validateString(data.field, 20L, "필드명");
            searchFilterField.setField(field);
        }
        SearchFilterField result = searchFilterCommandService.updateSearchFilterField(searchFilterField);
        res.setData(Optional.ofNullable(result.convert2Dto()));
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/field/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deleteSearchFilterField(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                           @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth.get());

        SearchFilterField searchFilterField = searchFilterQueryService.selectSearchFilterField(id);
        searchFilterCommandService.deleteSearchFilterField(id);
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }
}
