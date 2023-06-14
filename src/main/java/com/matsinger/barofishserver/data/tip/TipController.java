package com.matsinger.barofishserver.data.tip;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tip")
public class TipController {

    private final TipService tipService;

    private final Common utils;
    private final S3Uploader s3;

    private final JwtService jwt;

    @GetMapping("")
    public ResponseEntity<CustomResponse<List<Tip>>> selectTipList(@RequestParam(value = "type", required = false) TipType type) {
        CustomResponse<List<Tip>> res = new CustomResponse();
        try {
            List<Tip> tips = tipService.selectTipList(type);
            res.setData(Optional.ofNullable(tips));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<Tip>> selectTip(@PathVariable("id") Integer id) {
        CustomResponse<Tip> res = new CustomResponse();
        try {
            Tip tip = tipService.selectTip(id);
            res.setData(Optional.ofNullable(tip));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class AddTipReq {
        String title;
        String description;
        TipType type;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Tip>> addTip(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                      @RequestPart(value = "data") AddTipReq data,
                                                      @RequestPart(value = "image") MultipartFile image) {
        CustomResponse<Tip> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Tip tip = new Tip();
            String title = utils.validateString(data.title, 100L, "제목");
            String description = utils.validateString(data.description, 200L, "설명");
            String imageUrl = s3.upload(image, new ArrayList<>(Arrays.asList("tip")));
            tip.setTitle(title);
            tip.setType(data.type == null ? TipType.COMPARE : data.type);
            tip.setDescription(description);
            tip.setImage(imageUrl);
            tip.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            res.setData(Optional.ofNullable(tipService.add(tip)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Tip>> updateTip(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                         @PathVariable("id") Integer id,
                                                         @RequestPart(value = "data") AddTipReq data,
                                                         @RequestPart(value = "image", required = false) MultipartFile image) {
        CustomResponse<Tip> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Tip tip = new Tip();
            tipService.selectTip(id);
            if (data.title != null) {
                String title = utils.validateString(data.title, 100L, "제목");
                tip.setTitle(title);
            }
            if (data.description != null) {
                String description = utils.validateString(data.description, 200L, "설명");
                tip.setDescription(description);
            }
            if (data.type != null) {
                tip.setType(data.type);
            }
            if (image != null) {
                String imageUrl = s3.upload(image, new ArrayList<>(Arrays.asList("tip")));
                tip.setImage(imageUrl);
            }

            res.setData(Optional.ofNullable(tipService.update(id, tip)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteTip(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                    @PathVariable("id") Integer id) {
        CustomResponse res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            tipService.delete(id);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
