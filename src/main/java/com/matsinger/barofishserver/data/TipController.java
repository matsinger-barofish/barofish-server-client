package com.matsinger.barofishserver.data;

import com.matsinger.barofishserver.banner.Banner;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tip")
public class TipController {

    private final TipService tipService;

    private final Common utils;
    private final S3Uploader s3;

    @GetMapping("")
    public ResponseEntity<CustomResponse> selectTipList() {
        CustomResponse res = new CustomResponse();
        try {
            List<Tip> tips = tipService.selectTipList();
            res.setData(Optional.ofNullable(tips));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse> selectTip(@PathVariable("id") Integer id) {
        CustomResponse res = new CustomResponse();
        try {
            Tip tip = tipService.selectTip(id);
            res.setData(Optional.ofNullable(tip));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse> addTip(@RequestPart(value = "title") String title,
                                                 @RequestPart(value = "description") String description,
                                                 @RequestPart(value = "image") MultipartFile image) {
        CustomResponse res = new CustomResponse();
        try {
            Tip tip = new Tip();
            title = utils.validateString(title, 100L, "제목");
            description = utils.validateString(description, 200L, "설명");
            String imageUrl = null;
            imageUrl = s3.upload(image, new ArrayList<>(Arrays.asList("tip")));
            tip.setTitle(title);
            tip.setDescription(description);
            tip.setImage(imageUrl);
            res.setData(Optional.ofNullable(tipService.add(tip)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse> updateTip(@PathVariable("id") Integer id,
                                                    @RequestPart(value = "title", required = false) String title,
                                                    @RequestPart(value = "description", required = false) String description,
                                                    @RequestPart(value = "image", required = false) MultipartFile image) {
        CustomResponse res = new CustomResponse();
        try {
            Tip tip = new Tip();
            tipService.selectTip(id);
            if (title != null) {
                title = utils.validateString(title, 100L, "제목");
                tip.setTitle(title);
            }
            if (description != null) {
                description = utils.validateString(description, 200L, "설명");
                tip.setDescription(description);
            }
            if (image != null) {
                String imageUrl = s3.upload(image, new ArrayList<>(Arrays.asList("tip")));
                tip.setImage(imageUrl);
            }
            res.setData(Optional.ofNullable(tipService.update(id, tip)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomResponse> deleteTip(@PathVariable("id") Integer id) {
        CustomResponse res = new CustomResponse();
        try {
            tipService.delete(id);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }
}
