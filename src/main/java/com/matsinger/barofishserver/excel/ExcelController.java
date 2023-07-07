package com.matsinger.barofishserver.excel;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.product.object.Option;
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

    private final ExcelService excelService;
    private final JwtService jwt;

    @GetMapping("/test")
    public ResponseEntity<CustomResponse<Boolean>> test(@RequestPart(value = "file") MultipartFile file) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        try {
            System.out.println(file.getContentType());
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/upload-partner")
    public ResponseEntity<CustomResponse<Boolean>> uploadPartnerExcel(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @RequestPart(value = "file") MultipartFile file) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        try {
            if (file.isEmpty()) return res.throwError("파일을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (!file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                return res.throwError("엑셀 파일만 업로드 가능합니다.", "INPUT_CHECK_REQUIRED");
//            excelService.processUpsertPartnerExcelData(file);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
