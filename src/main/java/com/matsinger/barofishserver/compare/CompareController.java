package com.matsinger.barofishserver.compare;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/compare")
public class CompareController {

    private final JwtService jwt;

    private final CompareItemService compareService;

    @GetMapping("/set/list")
    public ResponseEntity<CustomResponse<List<CompareSet>>> selectCompareSetList(@RequestHeader("Authorization") Optional<String> auth) {
        CustomResponse<List<CompareSet>> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return ResponseEntity.ok(res);
        try {
            List<CompareSet> compareSets = compareService.selectCompareSetList(tokenInfo.get().getId());

            res.setData(Optional.ofNullable(compareSets));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/set/{id}")
    public ResponseEntity<CustomResponse<CompareSet>> selectCompareSet(@RequestHeader("Authorization") Optional<String> auth,
                                                                       @PathVariable("id") Integer id) {
        CustomResponse<CompareSet> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            CompareSet compareSet = compareService.selectCompareSet(id);
            List<Product> products = compareService.selectCompareItems(compareSet.getId());
            compareSet.setProducts(products);
            res.setData(Optional.ofNullable(compareSet));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/save")
    public ResponseEntity<CustomResponse<List<Product>>> selectSaveProductList(@RequestHeader("Authorization") Optional<String> auth) {
        CustomResponse<List<Product>> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<Product> products = compareService.selectSaveProducts(tokenInfo.get().getId());
            res.setData(Optional.ofNullable(products));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/add-set")
    public ResponseEntity<CustomResponse<CompareSet>> addCompareSet(@RequestHeader("Authorization") Optional<String> auth,
                                                                    @RequestBody List<Integer> productIds) {
        CustomResponse<CompareSet> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (productIds.size() != 3) return res.throwError("비교하기는 3개의 상품만 가능합니다.", "INPUT_CHECK_REQUIRED");
            CompareSet compareSet = compareService.addCompareSet(tokenInfo.get().getId(), productIds);
            res.setData(Optional.ofNullable(compareSet));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @AllArgsConstructor
    class SaveProductReq {
        private Integer productId;
    }

    @PostMapping("/save-product")
    public ResponseEntity<CustomResponse<SaveProduct>> saveProduct(@RequestHeader("Authorization") Optional<String> auth,
                                                                   @RequestBody SaveProductReq data) {
        CustomResponse<SaveProduct> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            compareService.addSaveProduct(tokenInfo.get().getId(), data.getProductId());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @AllArgsConstructor
    class DeleteCompareSetReq {
        private List<Integer> compareSetIds;
    }

    @DeleteMapping("/set")
    public ResponseEntity<CustomResponse<Boolean>> deleteCompareSet(@RequestHeader("Authorization") Optional<String> auth,
                                                                    @RequestBody DeleteCompareSetReq data) {
        CustomResponse<Boolean> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            for (Integer setId : data.getCompareSetIds()) {
                CompareSet compareSet = compareService.selectCompareSet(setId);
                if (tokenInfo.get().getId() != compareSet.getUserId())
                    return res.throwError("삭제 권한이 없습니다.", "NOT_ALLOWED");
            }
            for (Integer setId : data.getCompareSetIds()) {
                compareService.deleteCompareSet(setId);
            }
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @AllArgsConstructor
    class DeleteSaveProductReq {
        private List<Integer> productIds;
    }

    @DeleteMapping("/save")
    public ResponseEntity<CustomResponse<Boolean>> deleteSaveProducts(@RequestHeader("Authorization") Optional<String> auth,
                                                                      @RequestBody DeleteSaveProductReq data) {
        CustomResponse<Boolean> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<SaveProduct> saveProducts = new ArrayList<>();
            for (Integer productId : data.getProductIds()) {
                saveProducts.add(compareService.selectSaveProduct(tokenInfo.get().getId(), productId));
            }
            compareService.deleteSaveProduct(saveProducts);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
