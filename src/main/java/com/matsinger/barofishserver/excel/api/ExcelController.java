package com.matsinger.barofishserver.excel.api;

import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.excel.application.PartnerExcelService;
import com.matsinger.barofishserver.excel.application.ProductExcelService;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
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

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

        if (file.isEmpty()) throw new BusinessException("파일을 입력해주세요.");
        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            System.out.println("Partner File Upload mimType " + file.getContentType());
            throw new BusinessException("엑셀 파일만 업로드 가능합니다.");
        }
        partnerExcelService.processPartnerExcel(file);
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/upload-product")
    public ResponseEntity<CustomResponse<Boolean>> uploadProductExcel(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @RequestPart(value = "file") MultipartFile file) {
        CustomResponse<Boolean> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

        if (file.isEmpty()) throw new BusinessException("파일을 입력해주세요.");
        if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            System.out.println("Product File Upload mimType " + file.getContentType());
            throw new BusinessException("엑셀 파일만 업로드 가능합니다.");
        }
        productExcelService.processProductExcel(file);
        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }
}
