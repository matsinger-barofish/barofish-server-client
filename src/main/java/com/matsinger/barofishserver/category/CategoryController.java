package com.matsinger.barofishserver.category;

import com.matsinger.barofishserver.compare.filter.CompareFilter;
import com.matsinger.barofishserver.compare.filter.CompareFilterDto;
import com.matsinger.barofishserver.compare.filter.CompareFilterService;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final CompareFilterService compareFilterService;
    private final CategoryFilterService categoryFilterService;
    private final Common util;

    private final S3Uploader s3;
    private final JwtService jwt;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<List<Category>>> selectCategories() {
        CustomResponse<List<Category>> res = new CustomResponse<>();
        try {
            List<Category> categories = categoryService.findAll(null);
            res.setData(Optional.ofNullable(categories));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Category>> addCategory(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                @RequestPart(value = "categoryId", required = false) Integer categoryId,
                                                                @RequestPart(value = "name") String name,
                                                                @RequestPart(value = "image", required = false) MultipartFile file) {
        CustomResponse<Category> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Category category = new Category();
            name = util.validateString(name, 20L, "카테고리명");
            category.setName(name);
            String imageUrl = null;
            if (categoryId == null) {
                if (file == null) return res.throwError("상위 카테고리의 경우 이미지는 필수입니다.", "INPUT_CHECK_REQUIRED");
                imageUrl = s3.upload(file, new ArrayList<>(List.of("category")));
                category.setImage(imageUrl);
            } else {
                Category parentCategory = categoryService.findById(categoryId);
                category.setCategoryId(parentCategory.getId());
                category.setCategoryId(categoryId);
            }
            categoryService.add(category);
            res.setData(Optional.of(category));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Category>> updateCategory(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                   @PathVariable("id") Integer id,
                                                                   @RequestPart(value = "name", required = false) String name,
                                                                   @RequestPart(value = "image", required = false) MultipartFile image) {
        CustomResponse<Category> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Category category = categoryService.findById(id);
            String imageUrl = null;
            if (name != null) {
                name = util.validateString(name, 20L, "카테고리명");
                category.setName(name);
            }
            if (image != null) {
                imageUrl = s3.upload(image, new ArrayList<>(List.of("category")));
                category.setImage(imageUrl);
            }
            categoryService.update(id, category);
            res.setData(Optional.of(category));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse<Category>> deleteCategory(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                   @PathVariable("id") Integer id) {
        CustomResponse<Category> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Category category = categoryService.findById(id);
            if (category.getCategoryId() == null) {
                List<Category> categories = categoryService.findAll(id);
                if (categories.size() != 0) throw new Error("하위 카테고리가 존재합니다.");
                // 품목들이 존재하는 경우 처리 기획 필요
            }
            categoryService.delete(id);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    //비교하기 필터-------------------------
    @GetMapping("/compare-filter/{categoryId}")
    public ResponseEntity<CustomResponse<CategoryDto>> selectCategoryCompareFilter(@PathVariable("categoryId") Integer categoryId) {
        CustomResponse<CategoryDto> res = new CustomResponse<>();
        try {
            Category category = categoryService.findById(categoryId);
            List<Integer> compareFilterIds = categoryFilterService.selectCompareFilterIdList(categoryId);
            List<CompareFilterDto>
                    compareFilterDtos =
                    compareFilterService.selectCompareFilterListWithIds(compareFilterIds).stream().map(CompareFilter::convert2Dto).toList();
            CategoryDto
                    categoryDto =
                    CategoryDto.builder().filters(compareFilterDtos).id(categoryId).name(category.getName()).image(
                            category.getImage()).build();
            res.setData(Optional.ofNullable(categoryDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/compare-filter/list")
    public ResponseEntity<CustomResponse<List<CategoryDto>>> selectCategoryCompareFilter() {
        CustomResponse<List<CategoryDto>> res = new CustomResponse<>();
        try {
            List<Category> categories = categoryService.findParentCategories();
            List<CategoryDto> categoryDtos = categories.stream().map(category -> {

                List<Integer> compareFilterIds = categoryFilterService.selectCompareFilterIdList(category.getId());
                List<CompareFilterDto>
                        compareFilterDtos =
                        compareFilterService.selectCompareFilterListWithIds(compareFilterIds).stream().map(CompareFilter::convert2Dto).toList();
                return CategoryDto.builder().filters(compareFilterDtos).id(category.getId()).name(category.getName()).image(
                        category.getImage()).build();
            }).toList();
            res.setData(Optional.of(categoryDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class AddCategoryCompareFilterReq {
        Integer categoryId;
        Integer compareFilterId;
    }

    @PostMapping("/compare-filter/add/")
    public ResponseEntity<CustomResponse<CompareFilterDto>> addCategoryCompareFilter(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                     @RequestPart(value = "data") AddCategoryCompareFilterReq data) {
        CustomResponse<CompareFilterDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (data.categoryId == null) return res.throwError("카테고리 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.compareFilterId == null) return res.throwError("비교하기 필터 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Category category = categoryService.findById(data.categoryId);
            if (category.getCategoryId() != null) return res.throwError("1차 카테고리만 선택해주세요.", "INPUT_CHECK_REQUIRED");
            CompareFilter compareFilter = compareFilterService.selectCompareFilter(data.compareFilterId);
            CategoryFilterMap
                    categoryFilterMap =
                    CategoryFilterMap.builder().compareFilterId(data.compareFilterId).categoryId(data.categoryId).build();
            categoryFilterService.addCategoryFilterMap(categoryFilterMap);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/compare-filter/delete/")
    public ResponseEntity<CustomResponse<CompareFilterDto>> deleteCategoryCompareFilter(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                        @RequestPart(value = "data") AddCategoryCompareFilterReq data) {
        CustomResponse<CompareFilterDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (data.categoryId == null) return res.throwError("카테고리 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.compareFilterId == null) return res.throwError("비교하기 필터 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Category category = categoryService.findById(data.categoryId);
            CompareFilter compareFilter = compareFilterService.selectCompareFilter(data.compareFilterId);
            CategoryFilterId categoryFilterId = new CategoryFilterId();
            categoryFilterId.setCategoryId(data.categoryId);
            categoryFilterId.setCompareFilterId(data.compareFilterId);
            categoryFilterService.deleteCategoryFilterMap(categoryFilterId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
