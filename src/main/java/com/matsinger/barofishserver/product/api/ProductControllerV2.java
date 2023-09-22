package com.matsinger.barofishserver.product.api;

import com.matsinger.barofishserver.address.application.AddressQueryService;
import com.matsinger.barofishserver.admin.application.AdminQueryService;
import com.matsinger.barofishserver.admin.log.application.AdminLogCommandService;
import com.matsinger.barofishserver.admin.log.application.AdminLogQueryService;
import com.matsinger.barofishserver.category.application.CategoryQueryService;
import com.matsinger.barofishserver.compare.filter.application.CompareFilterQueryService;
import com.matsinger.barofishserver.data.curation.application.CurationCommandService;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.application.ProductQueryService;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.product.difficultDeliverAddress.application.DifficultDeliverAddressCommandService;
import com.matsinger.barofishserver.product.domain.ProductSortBy;
import com.matsinger.barofishserver.product.dto.ProductListDto;
import com.matsinger.barofishserver.product.productfilter.application.ProductFilterService;
import com.matsinger.barofishserver.search.application.SearchKeywordQueryService;
import com.matsinger.barofishserver.searchFilter.application.SearchFilterQueryService;
import com.matsinger.barofishserver.store.application.StoreService;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<CustomResponse<Page<ProductListDto>>> selectProductListByUser(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
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

        CustomResponse<Page<ProductListDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);

        try {
            Integer userId = null;
            if (tokenInfo != null && tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.USER)) {
                userId = tokenInfo.get().getId();
            }

            PageRequest pageRequest = PageRequest.of(page, take);
            Page<ProductListDto> result = productQueryService.getPagedProducts(pageRequest, sortBy, userId);

            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
