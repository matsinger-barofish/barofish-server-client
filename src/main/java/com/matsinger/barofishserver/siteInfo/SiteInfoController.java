package com.matsinger.barofishserver.siteInfo;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.notice.Notice;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/site_info")
public class SiteInfoController {

    private final SiteInfoService siteInfoService;

    private final JwtService jwt;
    private final S3Uploader s3;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<List<SiteInformation>>> selectSiteInfoList() {
        CustomResponse<List<SiteInformation>> res = new CustomResponse<>();
        try {
            List<SiteInformation> siteInfomations = siteInfoService.selectSiteInfoList();
            res.setData(Optional.ofNullable(siteInfomations));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<SiteInformation>> selectSiteInfo(@PathVariable("id") String id) {
        CustomResponse<SiteInformation> res = new CustomResponse<>();
        try {
            SiteInformation siteInfo = siteInfoService.selectSiteInfo(id);
            res.setData(Optional.ofNullable(siteInfo));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class SiteInfoReq {
        String content;
    }

    @PostMapping(value = "/update/{id}")
    public ResponseEntity<CustomResponse<SiteInformation>> updateSiteInfo(@PathVariable("id") String id,
                                                                          @RequestHeader(value = "Authorization") Optional<String> auth,
                                                                          @RequestPart(value = "data") SiteInfoReq data) {
        CustomResponse<SiteInformation> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            SiteInformation siteInformation = siteInfoService.selectSiteInfo(id);
            if (id.startsWith("HTML")) {
                String fileUrl = s3.uploadEditorStringToS3(data.getContent(), new ArrayList<>(Arrays.asList("tmp")));
                siteInformation.setContent(fileUrl);
                SiteInformation result = siteInfoService.updateSiteInfo(siteInformation);
                res.setData(Optional.of(result));
            } else if (id.startsWith("INT_")) {
                if (!data.getContent().matches("[0-9]+")) return res.throwError("숫자만 입력가능합니다.", "INPUT_CHECK_REQUIRED");
                siteInformation.setContent(data.getContent());
                SiteInformation result = siteInfoService.updateSiteInfo(siteInformation);
                res.setData(Optional.of(result));
            } else if (id.startsWith("INTERNAL")) {
                return res.throwError("수정 불가능한 데이터입니다.", "NOT_ALLOWED");
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
