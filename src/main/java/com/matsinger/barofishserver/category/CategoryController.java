package com.matsinger.barofishserver.category;

import com.matsinger.barofishserver.data.Curation;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final Common util;

    private final S3Uploader s3;

    @GetMapping("/")
    public ResponseEntity<CustomResponse> selectCategories() {
        CustomResponse res = new CustomResponse();
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
    public ResponseEntity<CustomResponse> addCategory(@RequestPart(value = "categoryId", required = false) Integer categoryId,
                                                      @RequestPart(value = "name") String name,
                                                      @RequestPart(value = "image", required = false) MultipartFile file) {
        CustomResponse res = new CustomResponse();
        try {
            Category category = new Category();
            name = util.validateString(name, 20L, "카테고리명");
            category.setName(name);
            String imageUrl = null;
            if (categoryId == null) {
                if (file == null) throw new Error("상위 카테고리의 경우 이미지는 필수입니다.");
                imageUrl = s3.upload(file, new ArrayList<>(Arrays.asList("category")));
                category.setImage(imageUrl);
            } else {
                Category parentCategory = categoryService.findById(category.getCategoryId());
                category.setParentCategory(parentCategory);
            }
            categoryService.add(category);
            res.setData(Optional.of(category));
            return ResponseEntity.ok(res);
        } catch (Error error) {
            res.setIsSuccess(false);
            res.setErrorMsg(error.getMessage());
            return ResponseEntity.ok(res);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse> updateCategory(@PathVariable("id") Integer id,
                                                         @RequestPart(value = "name", required = false) String name,
                                                         @RequestPart(value = "image", required = false) MultipartFile image) {
        CustomResponse res = new CustomResponse();
        try {
            Category category = categoryService.findById(id);
            String imageUrl = null;
            if (name != null) {
                name = util.validateString(name, 20L, "카테고리명");
                category.setName(name);
            }
            if (image != null) {
                imageUrl = s3.upload(image, new ArrayList<>(Arrays.asList("category")));
                category.setImage(imageUrl);
            }
            categoryService.update(Long.valueOf(id), category);
            res.setData(Optional.of(category));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteCategory(@PathVariable("id") Integer id) {
        CustomResponse res = new CustomResponse();
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
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }
}
