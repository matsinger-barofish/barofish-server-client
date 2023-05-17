package com.matsinger.barofishserver.banner;

import com.matsinger.barofishserver.category.CategoryService;
import com.matsinger.barofishserver.data.CurationService;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

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

    private final JwtService jwt;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<List<Banner>>> selectBannerList() {
        CustomResponse<List<Banner>> res = new CustomResponse();
        try {
            List<Banner> banners = bannerService.selectBannerList();
            res.setData(Optional.ofNullable(banners));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<Banner>> selectBanner(@PathVariable("id") Integer id) {
        CustomResponse res = new CustomResponse();
        try {
            Banner banner = bannerService.selectBanner(id);
            res.setData(Optional.ofNullable(banner));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Banner>> createBanner(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                       @RequestPart(value = "type") BannerType type,
                                                       @RequestPart(value = "image") MultipartFile image,
                                                       @RequestPart(value = "curationId", required = false) Integer curationId,
                                                       @RequestPart(value = "noticeId", required = false) Integer noticeId,
                                                       @RequestPart(value = "categoryId", required = false) Integer categoryId) {
        CustomResponse<Banner> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Banner banner = new Banner();
            if (type == BannerType.CATEGORY) {
                if (categoryId == null) return res.throwError("카테고리 아이디를 입력해주세요.", "INPUT_CHECK_NEEDED");
                categoryService.findById(categoryId);
                banner.setCategoryId(categoryId);
            } else if (type == BannerType.CURATION) {
                if (curationId == null) return res.throwError("큐레이션 아이디를 입력해주세요.", "INPUT_CHECK_NEEDED");
                curationService.findById(curationId);
                banner.setCurationId(curationId);
            } else if (type == BannerType.NOTICE) {
                if (noticeId == null) return res.throwError("배너 아이디를 입력해주세요.", "INPUT_CHECK_NEEDED");
                // TODO: 공지사항 생성 후 검증 유효성 추가 필요
//                curationService.findById(categoryId);
                banner.setNoticeId(noticeId);
            }

            banner.setState(BannerState.ACTIVE);
            banner.setType(type);

            String imageUrl = s3.upload(image, new ArrayList<>(Arrays.asList("banner")));
            banner.setImage(imageUrl);
            Banner result = bannerService.add(banner);
            res.setData(Optional.ofNullable(result));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Banner>> updateBanner(
            @RequestHeader(value = "Authorization") Optional<String> auth
    ) {
        CustomResponse<Banner> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
