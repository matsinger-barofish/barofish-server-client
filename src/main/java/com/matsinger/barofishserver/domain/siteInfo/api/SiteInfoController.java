package com.matsinger.barofishserver.domain.siteInfo.api;

import com.matsinger.barofishserver.domain.siteInfo.application.SiteInfoQueryService;
import com.matsinger.barofishserver.global.error.ErrorCode;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.siteInfo.application.SiteInfoCommandService;
import com.matsinger.barofishserver.domain.siteInfo.dto.SiteInfoDto;
import com.matsinger.barofishserver.domain.siteInfo.domain.SiteInformation;
import com.matsinger.barofishserver.domain.siteInfo.dto.SiteInfoReq;
import com.matsinger.barofishserver.jwt.exception.JwtBusinessException;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.RegexConstructor;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.*;
import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/site_info")
public class SiteInfoController {

    private final SiteInfoQueryService siteInfoQueryService;
    private final SiteInfoCommandService siteInfoCommandService;

    private final JwtService jwt;
    private final S3Uploader s3;
    private final RegexConstructor reg;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<List<SiteInfoDto>>> selectSiteInfoList() {
        CustomResponse<List<SiteInfoDto>> res = new CustomResponse<>();

        List<SiteInfoDto>
                siteInfomations =
                siteInfoQueryService.selectSiteInfoList().stream().map(SiteInformation::convert2Dto).toList();
        res.setData(Optional.ofNullable(siteInfomations));
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<SiteInfoDto>> selectSiteInfo(@PathVariable("id") String id) {
        CustomResponse<SiteInfoDto> res = new CustomResponse<>();

        SiteInfoDto siteInfo = siteInfoQueryService.selectSiteInfo(id).convert2Dto();
        res.setData(Optional.ofNullable(siteInfo));
        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "/update/{id}")
    public ResponseEntity<CustomResponse<SiteInfoDto>> updateSiteInfo(@PathVariable("id") String id,
                                                                      @RequestHeader(value = "Authorization") Optional<String> auth,
                                                                      @RequestPart(value = "data") SiteInfoReq data) {
        CustomResponse<SiteInfoDto> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

        SiteInformation siteInformation = siteInfoQueryService.selectSiteInfo(id);
        if (id.startsWith("HTML")) {
            String fileUrl = s3.uploadEditorStringToS3(data.getContent(), new ArrayList<>(List.of("tmp")));
            siteInformation.setContent(fileUrl);
            SiteInformation result = siteInfoCommandService.updateSiteInfo(siteInformation);
            res.setData(Optional.of(result.convert2Dto()));
        } else if (id.startsWith("INT_")) {
            if (!data.getContent().matches("[0-9]+")) throw new IllegalArgumentException("숫자만 입력가능합니다.");
            siteInformation.setContent(data.getContent());
            SiteInformation result = siteInfoCommandService.updateSiteInfo(siteInformation);
            res.setData(Optional.of(result.convert2Dto()));
        } else if (id.startsWith("INTERNAL")) {
            throw new IllegalArgumentException("수정 불가능한 데이터입니다.");
        } else if (id.startsWith("URL")) {
            if (!Pattern.matches(reg.httpUrl, data.getContent()))
                throw new IllegalArgumentException("URL 형식을 확인해주세요.");
            siteInformation.setContent(data.getContent());
            SiteInformation result = siteInfoCommandService.updateSiteInfo(siteInformation);
            res.setData(Optional.ofNullable(result.convert2Dto()));
        } else if (id.startsWith("TC")) {
            if (data.getTcContent() == null) throw new IllegalArgumentException("내용을 입력해주세요.");
            JSONArray json = new JSONArray(data.getTcContent());
            String jsonString = json.toString();
            siteInformation.setContent(jsonString);
            SiteInformation result = siteInfoCommandService.updateSiteInfo(siteInformation);
            res.setData(Optional.ofNullable(result.convert2Dto()));
        }
        return ResponseEntity.ok(res);
    }
}
