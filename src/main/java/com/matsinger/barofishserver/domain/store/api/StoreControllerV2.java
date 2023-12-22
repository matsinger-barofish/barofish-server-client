package com.matsinger.barofishserver.domain.store.api;

import com.matsinger.barofishserver.domain.store.application.StoreQueryService;
import com.matsinger.barofishserver.domain.store.domain.StoreRecommendType;
import com.matsinger.barofishserver.domain.store.dto.SimpleStore;
import com.matsinger.barofishserver.domain.store.dto.StoreDto;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/store")
public class StoreControllerV2 {

    private final JwtService jwt;
    private final Common utils;
    private final StoreQueryService storeQueryService;

    @PostMapping("/download")
    public void downloadStoresWithExcel(
            @RequestHeader(value = "Authorization", required = false) Optional<String> auth,
            @RequestPart(value = "storeIds", required = false) List<Integer> storeIds,
            HttpServletResponse httpServletResponse) throws IOException {
        CustomResponse<List<StoreDto>> res = new CustomResponse<>();
        jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

        String nowDate = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
//        String fileName = nowDate + "_바로피쉬_정산.xlsx";
        String fileName = nowDate + "_barofish_stores.xlsx";

        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        httpServletResponse.setContentType("application/octet-stream");

        Workbook workbook = storeQueryService.downloadStoresWithExcel(storeIds);

        try {
            workbook.write(httpServletResponse.getOutputStream());
        } catch (Exception e) {
            e.getMessage();
        } finally {
            workbook.close();
        }
    }

    @GetMapping("/recommend")
    public ResponseEntity<CustomResponse<List<SimpleStore>>> selectRecommendStoreListV2(@RequestHeader("Authorization") Optional<String> auth,
                                                                                      @RequestParam(value = "type") StoreRecommendType type,
                                                                                      @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                                                                      @RequestParam(value = "take", defaultValue = "10", required = false) Integer take,
                                                                                      @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        CustomResponse<List<SimpleStore>> response = new CustomResponse<>();

        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW, TokenAuthType.USER), auth);

        PageRequest pageRequest = PageRequest.of(page-1, take);
        List<SimpleStore> pagedStoreDtos = storeQueryService.selectRecommendStoreList(pageRequest, type, keyword, tokenInfo.getId());

        response.setIsSuccess(true);
        response.setData(Optional.of(pagedStoreDtos));
        return ResponseEntity.ok(response);
    }
}
