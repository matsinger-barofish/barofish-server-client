package com.matsinger.barofishserver.data;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
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

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/curation")
public class CurationController {

    private final CurationService curationService;


    private final ProductService productService;

    private final Common util;

    private final S3Uploader s3;
    private final JwtService jwt;

    // GET
    @Description("큐레이션 목록")
    @GetMapping("/")
    public ResponseEntity<CustomResponse<List<Curation>>> selectCurationList() {
        CustomResponse<List<Curation>> res = new CustomResponse();
        try {
            List<Curation> curations = curationService.selectCurations();
            res.setData(Optional.ofNullable(curations));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<Curation>> selectCuration(@PathVariable("id") Integer id) {
        CustomResponse<Curation> res = new CustomResponse<>();
        try {
            Curation curation = curationService.selectCuration(id);
            res.setData(Optional.ofNullable(curation));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Description("큐레이션 상품 목록")
    @GetMapping("/{id}/products")
    public ResponseEntity<CustomResponse<List<Product>>> selectCurationProducts(@PathVariable("id") Long id) {
        CustomResponse<List<Product>> res = new CustomResponse();
        try {
            List<Product> products = curationService.selectCurationProducts(id);
            res.setData(Optional.ofNullable(products));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    //POST
    @Description("큐레이션 추가")
    @PostMapping(value = "/add", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CustomResponse<Curation>> createCuration(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                   @RequestPart(value = "image") MultipartFile file,
                                                                   @RequestPart(value = "shortName") String shortName,
                                                                   @RequestPart(value = "title") String title,
                                                                   @RequestPart(value = "description") String description,
                                                                   @RequestPart(value = "type") CurationType type) {
        CustomResponse<Curation> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Curation curation = new Curation();
            shortName = util.validateString(shortName, 20L, "약어");
            curation.setShortName(shortName);
            title = util.validateString(title, 100L, "제목");
            curation.setTitle(title);
            description = util.validateString(description, 200L, "설명");
            curation.setDescription(description);
            String image = s3.upload(file, new ArrayList<>(Arrays.asList("curation")));
            curation.setImage(image);
            curation.setType(type);
            curation.setSortNo(1L);
            Curation data = curationService.add(curation);
            res.setData(Optional.ofNullable(data));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Curation>> updateCuration(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                   @PathVariable("id") Integer id,
                                                                   @RequestPart(value = "image", required = false) MultipartFile file,
                                                                   @RequestPart(value = "shortName", required = false) String shortName,
                                                                   @RequestPart(value = "title", required = false) String title,
                                                                   @RequestPart(value = "description", required = false) String description,
                                                                   @RequestPart(value = "type", required = false) CurationType type) {
        CustomResponse<Curation> res = new CustomResponse<>();
        try {
            Curation curation = curationService.selectCuration(id);
            if (file != null) {
                if (!s3.validateImageType(file)) return res.throwError("허용되지 않는 확장자입니다.", "INPUT_CHECK_REQUIRED");
                String imageUrl = s3.upload(file, new ArrayList<>(Arrays.asList("curation")));
                curation.setImage(imageUrl);
            }
            if (shortName != null) {
                shortName = util.validateString(shortName, 20L, "이름");
                curation.setShortName(shortName);
            }
            if (title != null) {
                title = util.validateString(title, 100L, "제목");
                curation.setTitle(title);
            }
            if (description != null) {
                description = util.validateString(description, 200L, "설명");
                curation.setDescription(description);
            }
            if (type != null) {
                curation.setType(type);
            }
            curationService.update(curation);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Description("큐레이션 목록 상품 추가")
    @PostMapping("/{id}/add-product")
    public ResponseEntity<CustomResponse<List<CurationProductMap>>> addProductToCuration(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                         @PathVariable("id") Integer id,
                                                                                         @RequestBody ArrayList<Integer> productIds) {
        CustomResponse<List<CurationProductMap>> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Curation curation = curationService.selectCuration(id);
            if (curation == null) throw new Error("큐레이션 정보를 찾을 수 없습니다.");
            for (Integer productId : productIds) {
                Product product = productService.selectProduct(productId);
                if (product == null) throw new Error("상품 정보를 찾을 수 없습니다.");
            }
            List<CurationProductMap> result = curationService.addProduct(id, productIds);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    //DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<CustomResponse<Curation>> deleteCuration(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                   @PathVariable("id") Integer id) {
        CustomResponse<Curation> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Curation curation = curationService.selectCuration(id);
            if (curation == null) throw new Error("큐레이션 데이터를 찾을 수 없습니다.");
            curationService.delete(id);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
