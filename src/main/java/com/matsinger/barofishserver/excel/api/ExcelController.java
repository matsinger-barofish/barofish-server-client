package com.matsinger.barofishserver.excel.api;

import com.matsinger.barofishserver.excel.application.PartnerExcelService;
import com.matsinger.barofishserver.excel.application.ProductExcelService;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/excel")
public class ExcelController {

    private final PartnerExcelService partnerExcelService;
    private final ProductExcelService productExcelService;
    private final ProductService productService;
    private final JwtService jwt;


    @PostMapping("/upload-partner")
    public ResponseEntity<CustomResponse<Boolean>> uploadPartnerExcel(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @RequestPart(value = "file") MultipartFile file) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (file.isEmpty()) return res.throwError("파일을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                System.out.println("Partner File Upload mimType " + file.getContentType());
                return res.throwError("엑셀 파일만 업로드 가능합니다.", "INPUT_CHECK_REQUIRED");
            }
            partnerExcelService.processPartnerExcel(file);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/upload-product")
    public ResponseEntity<CustomResponse<Boolean>> uploadProductExcel(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @RequestPart(value = "file") MultipartFile file) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (file.isEmpty()) return res.throwError("파일을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                System.out.println("Product File Upload mimType " + file.getContentType());
                return res.throwError("엑셀 파일만 업로드 가능합니다.", "INPUT_CHECK_REQUIRED");
            }
            productExcelService.processProductExcel(file);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
