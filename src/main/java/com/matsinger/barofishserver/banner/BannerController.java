package com.matsinger.barofishserver.banner;

import com.matsinger.barofishserver.category.CategoryService;
import com.matsinger.barofishserver.data.CurationService;
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
@RequestMapping("/api/v1/banner")
public class BannerController {
    private final BannerService bannerService;
    private final BannerRepository bannerRepository;
    private final Common util;

    private final CategoryService categoryService;
    private final CurationService curationService;
    private final S3Uploader s3;

    @GetMapping("/")
    public ResponseEntity<CustomResponse> selectBannerList() {
        CustomResponse res = new CustomResponse();
        try {
            List<Banner> banners = bannerService.selectBannerList();
            res.setData(Optional.ofNullable(banners));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse> selectBanner(@PathVariable("id") Integer id) {
        CustomResponse res = new CustomResponse();
        try {
            Banner banner = bannerService.selectBanner(id);
            res.setData(Optional.ofNullable(banner));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse> createBanner(@RequestPart(value = "type") BannerType type,
                                                       @RequestPart(value = "image") MultipartFile image,
                                                       @RequestPart(value = "curationId", required = false) Integer curationId,
                                                       @RequestPart(value = "noticeId", required = false) Integer noticeId,
                                                       @RequestPart(value = "categoryId", required = false) Integer categoryId) {
        CustomResponse res = new CustomResponse();
        try {
            Banner banner = new Banner();
            if (type == BannerType.CATEGORY) {
                if (categoryId == null) throw new Error("카테고리 아이디를 입력해주세요.");
                categoryService.findById(categoryId);
                banner.setCategoryId(categoryId);
            } else if (type == BannerType.CURATION) {
                if (curationId == null) throw new Error("큐레이션 아이디를 입력해주세요.");
                curationService.findById(curationId);
                banner.setCurationId(curationId);
            } else if (type == BannerType.NOTICE) {
                if (noticeId == null) throw new Error("공지사항 아이디를 입력해주세요.");
                // TODO: 공지사항 생성 후 검증 유효성 추가 필요
//                curationService.findById(categoryId);
                banner.setNoticeId(noticeId);
            }

            banner.setState(BannerState.ACTIVE);
            banner.setType(type);

            String imageUrl = s3.upload(image, new ArrayList<>(Arrays.asList("banner")));
            banner.setImage(imageUrl);
            bannerService.add(banner);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse> updateBanner() {
        CustomResponse res = new CustomResponse();
        try {
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.setIsSuccess(false);
            res.setErrorMsg(e.getMessage());
            return ResponseEntity.ok(res);
        }
    }
}
