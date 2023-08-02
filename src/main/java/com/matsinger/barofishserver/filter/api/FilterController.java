package com.matsinger.barofishserver.filter.api;

import com.matsinger.barofishserver.category.application.CategoryQueryService;
import com.matsinger.barofishserver.category.domain.Category;
import com.matsinger.barofishserver.filter.domain.Filter;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/filter")
public class FilterController {
    private final CategoryQueryService categoryQueryService;

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<Filter>> selectFilterList() {
        CustomResponse<Filter> res = new CustomResponse<>();
        try {
            Filter filter = new Filter();
            List<Category> categories = categoryQueryService.findParentCategories();
            for (Category category : categories) {
                category.setCategoryList(null);
            }
            filter.setCategories(categories);
            res.setData(Optional.of(filter));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }

    }
}
