package com.matsinger.barofishserver.domain.product.api;

import com.matsinger.barofishserver.domain.address.application.AddressQueryService;
import com.matsinger.barofishserver.domain.admin.application.AdminQueryService;
import com.matsinger.barofishserver.domain.admin.log.application.AdminLogCommandService;
import com.matsinger.barofishserver.domain.admin.log.application.AdminLogQueryService;
import com.matsinger.barofishserver.domain.category.application.CategoryQueryService;
import com.matsinger.barofishserver.domain.compare.filter.application.CompareFilterQueryService;
import com.matsinger.barofishserver.domain.data.curation.application.CurationCommandService;
import com.matsinger.barofishserver.domain.product.application.ProductQueryService;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.application.DifficultDeliverAddressCommandService;
import com.matsinger.barofishserver.domain.product.domain.ProductSortBy;
import com.matsinger.barofishserver.domain.product.dto.ExpectedArrivalDateResponse;
import com.matsinger.barofishserver.domain.product.dto.ProductListDtoV2;
import com.matsinger.barofishserver.domain.product.productfilter.application.ProductFilterService;
import com.matsinger.barofishserver.domain.search.application.SearchKeywordQueryService;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.searchFilter.application.SearchFilterQueryService;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/product")
public class ProductControllerV2 {

    private final ProductService productService;
    private final StoreService storeService;
    private final CategoryQueryService categoryQueryService;
    private final CurationCommandService curationCommandService;
    private final CompareFilterQueryService compareFilterQueryService;
    private final ProductFilterService productFilterService;
    private final SearchFilterQueryService searchFilterQueryService;
    private final ProductQueryService productQueryService;
    private final SearchKeywordQueryService searchKeywordQueryService;
    private final AddressQueryService addressQueryService;
    private final DifficultDeliverAddressCommandService difficultDeliverAddressCommandService;
    private final AdminLogQueryService adminLogQueryService;
    private final AdminLogCommandService adminLogCommandService;
    private final AdminQueryService adminQueryService;
    private final JwtService jwt;

    private final Common utils;

    private final S3Uploader s3;

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<Object>> selectProductListByUserV2(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                                          @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                                          @RequestParam(value = "take", defaultValue = "10") Integer take,
                                                                                          @RequestParam(value = "sortby", defaultValue = "RECOMMEND", required = false) ProductSortBy sortBy,
                                                                                          @RequestParam(value = "categoryIds", required = false) String categoryIds,
                                                                                          @RequestParam(value = "filterFieldIds", required = false) String filterFieldIds,
                                                                                          @RequestParam(value = "typeIds", required = false) String typeIds,
                                                                                          @RequestParam(value = "locationIds", required = false) String locationIds,
                                                                                          @RequestParam(value = "processIds", required = false) String processIds,
                                                                                          @RequestParam(value = "usageIds", required = false) String usageIds,
                                                                                          @RequestParam(value = "storageIds", required = false) String storageIds,
                                                                                          @RequestParam(value = "curationId", required = false) Integer curationId,
                                                                                          @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                                                                          @RequestParam(value = "storeId", required = false) Integer storeId) {

        CustomResponse<Object> res = new CustomResponse<>();

        if (auth.isEmpty()) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW, TokenAuthType.USER), auth.get());

        Integer userId = tokenInfo.getId();

        PageRequest pageRequest = PageRequest.of(page - 1, take);
        Page<ProductListDtoV2> result = productQueryService.getPagedProducts(pageRequest, sortBy, keyword);

        res.setData(Optional.ofNullable(result));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/arrival-date/{id}")
    public ResponseEntity<CustomResponse<Object>> getExpectedArrivalDate(@PathVariable(value = "id") Integer productId,
                                                                         @RequestParam(value = "Authorization") Optional<String> auth) {

        CustomResponse<Object> res = new CustomResponse<>();

        LocalDateTime now = LocalDateTime.now();

        ExpectedArrivalDateResponse expectedArrivalDate = productQueryService.getExpectedArrivalDate(now, productId);

        res.setData(Optional.of(expectedArrivalDate));
        return ResponseEntity.ok(res);
    }
}
