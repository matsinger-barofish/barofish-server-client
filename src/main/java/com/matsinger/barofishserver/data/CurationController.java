package com.matsinger.barofishserver.data;

import com.matsinger.barofishserver.product.Option;
import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.MediaType;
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
@RequestMapping("/api/v1/curation")
public class CurationController {

    private final CurationService curationService;


    private final ProductService productService;

    private final Common util;

    private final S3Uploader s3;

    // GET
    @Description("큐레이션 목록")
    @GetMapping("/")
    public ResponseEntity<CustomResponse> selectCurationList() {
        CustomResponse res = new CustomResponse();
        try {
            List<Curation> curations = curationService.selectCurations();
            res.setData(Optional.ofNullable(curations));
            return ResponseEntity.ok(res);
        } catch (Error error) {
            res.setIsSuccess(false);
            res.setErrorMsg(error.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @Description("큐레이션 상품 목록")
    @GetMapping("/{id}/products")
    public ResponseEntity<CustomResponse> selectCurationProducts(@PathVariable("id") Long id) {
        CustomResponse res = new CustomResponse();
        try {
            List<Product> products = curationService.selectCurationProducts(id);
            res.setData(Optional.ofNullable(products));
            return ResponseEntity.ok(res);
        } catch (Error error) {
            res.setIsSuccess(false);
            res.setErrorMsg(error.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    //POST
    @Description("큐레이션 추가")
    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CustomResponse> createCuration(@RequestPart(value = "data") Curation curation,
                                                         @RequestPart(value = "image") MultipartFile file) {
        CustomResponse res = new CustomResponse();
        try {
            String shortName = util.validateString(curation.getShortName(), 20L, "약어");
            curation.setShortName(shortName);
            String title = util.validateString(curation.getTitle(), 100L, "제목");
            curation.setTitle(title);
            String description = util.validateString(curation.getDescription(), 200L, "설명");
            curation.setDescription(description);
            String image = s3.upload(file, new ArrayList<>(Arrays.asList("curation")));
            curation.setImage(image);
            Curation data = curationService.add(curation);
            res.setData(Optional.ofNullable(data));
            return ResponseEntity.ok(res);
        } catch (Error error) {
            res.setIsSuccess(false);
            res.setErrorMsg(error.getMessage());
            return ResponseEntity.ok(res);
        } catch (IOException e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @Description("큐레이션 목록 상품 추가")
    @PostMapping("/{id}/add-product")
    public ResponseEntity<CustomResponse> addProductToCuration(@PathVariable("id") Long id,
                                                               @RequestBody ArrayList<Long> productIds) {
        CustomResponse res = new CustomResponse();
        try {
            Optional<Curation> curation = curationService.findById(id);
            if (curation == null) throw new Error("큐레이션 정보를 찾을 수 없습니다.");
            for (Long productId : productIds) {
                Optional<Product> product = productService.findById(productId);
                if (product == null) throw new Error("상품 정보를 찾을 수 없습니다.");
            }
            curationService.addProduct(id,productIds);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    //DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomResponse> deleteCuration(@PathVariable("id") Long id) {
        CustomResponse res = new CustomResponse();
        try {
            Optional<Curation> curation = curationService.findById(id);
            if (curation == null) throw new Error("큐레이션 데이터를 찾을 수 없습니다.");
            curationService.delete(id);
            return ResponseEntity.ok(res);
        } catch (Error error) {
            res.setIsSuccess(false);
            res.setErrorMsg(error.getMessage());
            return ResponseEntity.ok(res);
        }
    }
}
