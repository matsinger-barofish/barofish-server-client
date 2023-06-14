package com.matsinger.barofishserver.store;

import com.matsinger.barofishserver.jwt.*;
import com.matsinger.barofishserver.product.LikePostType;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.review.Review;
import com.matsinger.barofishserver.review.ReviewService;
import com.matsinger.barofishserver.store.object.*;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/store")
public class StoreController {
    private final StoreService storeService;
    private final ProductService productService;
    private final ReviewService reviewService;
    private final JwtService jwt;
    private final JwtProvider jwtProvider;

    private final Common utils;
    private final S3Uploader s3;

    @GetMapping("")
    public ResponseEntity<CustomResponse<List<StoreDto>>> selectStoreList(@RequestHeader(value = "Authorization", required = false) Optional<String> auth) {
        CustomResponse<List<StoreDto>> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                auth.isPresent() ? jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth) : null;
        if (auth.isEmpty() && tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        Boolean
                isAdmin =
                tokenInfo != null && tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.ADMIN);
        try {
            List<StoreDto> stores = storeService.selectStoreList(true).stream().map(store -> {
                return storeService.convert2Dto(store);
            }).toList();

            res.setData(Optional.ofNullable(stores));

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/recommend")
    public ResponseEntity<CustomResponse<List<SimpleStore>>> selectRecommendStoreList(@RequestHeader("Authorization") Optional<String> auth,
                                                                                      @RequestParam(value = "type") StoreRecommendType type,
                                                                                      @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
                                                                                      @RequestParam(value = "take", defaultValue = "10", required = false) Integer take,
                                                                                      @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword) {
        CustomResponse<List<SimpleStore>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        try {
            List<SimpleStore> stores = storeService.selectRecommendStore(type, page - 1, take, keyword);
            if (tokenInfo != null && tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.USER)) {
                for (SimpleStore store : stores) {
                    store.setIsLike(storeService.checkLikeStore(store.getStoreId(), tokenInfo.get().getId()));
                }
            }
            res.setData(Optional.ofNullable(stores));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponse<Jwt>> loginStore(@RequestPart(value = "loginId") String loginId,
                                                          @RequestPart(value = "password") String password) {
        CustomResponse<Jwt> res = new CustomResponse<>();
        try {
            Store store = storeService.selectStoreByLoginId(loginId);
            if (store == null) return res.throwError("아이디 및 비밀번호를 확인해주세요.", "INPUT_CHECK_REQUIRED");
            if (!BCrypt.checkpw(password, store.getPassword()))
                return res.throwError("아이디 및 비밀번호를 확인해주세요.", "INPUT_CHECK_REQUIRED");
            if (!store.getState().equals(StoreState.ACTIVE)) {
                if (store.getState().equals(StoreState.BANNED)) return res.throwError("정지된 계정입니다.", "NOT_ALLOWED");
                if (store.getState().equals(StoreState.DELETED)) return res.throwError("삭제된 계정입니다.", "NOT_ALLOWED");
            }
            String accessToken = jwtProvider.generateAccessToken(String.valueOf(store.getId()), TokenAuthType.PARTNER);
            String
                    refreshToken =
                    jwtProvider.generateRefreshToken(String.valueOf(store.getId()), TokenAuthType.PARTNER);
            Jwt token = new Jwt();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            res.setData(Optional.of(token));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping(value = {"/management/{id}", "management"})
    public ResponseEntity<CustomResponse<StoreDto>> selectStoreByAdmin(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                       @PathVariable(value = "id", required = false) Integer id) {
        CustomResponse<StoreDto> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER)) {
                id = tokenInfo.get().getId();
            } else {
                if (id == null) return res.throwError("스토어 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            }
            Store store = storeService.selectStore(id);

            res.setData(Optional.ofNullable(storeService.convert2Dto(store)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<SimpleStore>> selectStore(@PathVariable("id") Integer id,
                                                                   @RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<SimpleStore> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        try {
            StoreInfo storeInfo = storeService.selectStoreInfo(id);
            SimpleStore data = storeInfo.convert2Dto();
            List<Product> products = productService.selectProductListWithStoreIdAndStateActive(id);
            data.setProducts(products.stream().map(product -> {
                return product.convert2ListDto();
            }).toList());
            Page<Review> reviews = reviewService.selectReviewListByStore(id, 0, 50);
            if (tokenInfo != null && tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.USER))
                data.setIsLike(storeService.checkLikeStore(id, tokenInfo.get().getId()));
            data.setReviews(reviews.stream().map(review -> {
                return review.convert2Dto();
            }).toList());
            data.setReviewCount((int) reviews.getTotalElements());
            data.setProductCount(products.size());
            data.setImageReviews(reviews.stream().map(review -> {
                return review.convert2Dto();
            }).toList());
            res.setData(Optional.ofNullable(data));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<StoreDto>> addStore(@RequestHeader("Authorization") Optional<String> auth,
                                                             @RequestPart(value = "loginId") String loginId,
                                                             @RequestPart(value = "password") String password,
                                                             @RequestPart(value = "backgroundImage") MultipartFile backgroundImage,
                                                             @RequestPart(value = "profileImage") MultipartFile profileImage,
                                                             @RequestPart(value = "name") String name,
                                                             @RequestPart(value = "location") String location,
                                                             @RequestPart(value = "keyword") String keyword,
                                                             @RequestPart(value = "visitNote", required = false) String visitNote) {
        CustomResponse<StoreDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            loginId = utils.validateString(loginId, 50L, "로그인 아이디");
            Boolean check = storeService.checkStoreLoginIdValid(loginId);
            if (!check) return res.throwError("중복된 아이디입니다.", "NOT_ALLOWED");
            if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=\\[\\]{}|\\\\:;\"'<>,.?/])(?=" +
                    ".*[^\\s]).{8,20}$")) return res.throwError("비밀번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            name = utils.validateString(name, 50L, "이름");
            location = utils.validateString(location, 50L, "위치");
            Store storeData = new Store();
            storeData.setLoginId(loginId);
            storeData.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            storeData.setState(StoreState.ACTIVE);
            StoreInfo storeInfoData = new StoreInfo();
            storeInfoData.setName(name);
            storeInfoData.setLocation(location);
            storeInfoData.setKeyword(keyword);
            storeData.setJoinAt(utils.now());
            storeInfoData.setVisitNote("");
            Store store = storeService.addStore(storeData);
            String
                    backgroundImageUrl =
                    s3.upload(backgroundImage, new ArrayList<>(Arrays.asList("store", String.valueOf(store.getId()))));
            String
                    profileImageUrl =
                    s3.upload(profileImage, new ArrayList<>(Arrays.asList("store", String.valueOf(store.getId()))));
            String
                    visitNoteUrl =
                    s3.uploadEditorStringToS3(visitNote,
                            new ArrayList<>(Arrays.asList("store", String.valueOf(store.getId()))));

            storeInfoData.setStoreId(store.getId());
            storeInfoData.setVisitNote(visitNoteUrl);
            storeInfoData.setBackgroudImage(backgroundImageUrl);
            storeInfoData.setProfileImage(profileImageUrl);
            StoreInfo storeInfo = storeService.addStoreInfo(storeInfoData);
            res.setData(Optional.of(storeService.convert2Dto(store)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class StoreStateUpdateReq {
        List<Integer> storeIds;
        StoreState state;
    }

    @PostMapping("/update/state")
    public ResponseEntity<CustomResponse<Boolean>> updateStoreState(@RequestHeader("Authorization") Optional<String> auth,
                                                                    @RequestPart(value = "data") StoreStateUpdateReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<Store> stores = new ArrayList<>();
            for (Integer storeId : data.getStoreIds()) {
                Store store = storeService.selectStore(storeId);
                store.setState(data.state);
                stores.add(store);
            }
            storeService.updateStores(stores);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping(value = {"/update/{id}", "/update"})
    public ResponseEntity<CustomResponse<StoreDto>> updateStoreInfo(@RequestHeader("Authorization") Optional<String> auth,
                                                                    @PathVariable(value = "id", required = false) Integer id,
                                                                    @RequestPart(value = "backgroundImage", required = false) MultipartFile backgroundImage,
                                                                    @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                                                    @RequestPart(value = "name", required = false) String name,
                                                                    @RequestPart(value = "location", required = false) String location,
                                                                    @RequestPart(value = "keyword", required = false) String keyword,
                                                                    @RequestPart(value = "visitNote", required = false) String visitNote) {
        CustomResponse<StoreDto> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER)) {
                id = tokenInfo.get().getId();
            } else {
                if (id == null) return res.throwError("스토어 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            }
            StoreInfo storeInfo = storeService.selectStoreInfo(id);
            if (backgroundImage != null) {
                String
                        imageUrl =
                        s3.upload(backgroundImage, new ArrayList<>(Arrays.asList("store", String.valueOf(id))));
                storeInfo.setBackgroudImage(imageUrl);
            }
            if (profileImage != null) {
                String imageUrl = s3.upload(profileImage, new ArrayList<>(Arrays.asList("store", String.valueOf(id))));
                storeInfo.setProfileImage(imageUrl);
            }
            if (name != null) {
                name = utils.validateString(name, 50L, "이름");
                storeInfo.setName(name);
            }
            if (location != null) {
                location = utils.validateString(location, 50L, "위치");
                storeInfo.setLocation(location);
            }
            if (keyword != null) {
                keyword = utils.validateString(keyword, 50L, "위치");
                storeInfo.setKeyword(keyword);
            }
            if (visitNote != null) {
                String
                        visitNoteUrl =
                        s3.uploadEditorStringToS3(visitNote,
                                new ArrayList<>(Arrays.asList("store", String.valueOf(id))));
                storeInfo.setVisitNote(visitNoteUrl);
            }
            StoreInfo result = storeService.updateStoreInfo(storeInfo);
            Store store = storeService.selectStore(result.getStoreId());
            res.setData(Optional.ofNullable(storeService.convert2Dto(store)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/like")
    public ResponseEntity<CustomResponse<Boolean>> likeStoreByUser(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                   @RequestParam(value = "storeId") Integer storeId,
                                                                   @RequestParam(value = "type") LikePostType type) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Boolean check = storeService.checkLikeStore(storeId, tokenInfo.get().getId());
            if (!check && type.equals(LikePostType.LIKE)) {
                storeService.likeStore(storeId, tokenInfo.get().getId());
                res.setData(Optional.of(true));
            } else if (check && type.equals(LikePostType.UNLIKE)) {
                storeService.unlikeStore(storeId, tokenInfo.get().getId());
                res.setData(Optional.of(true));
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/star")
    public ResponseEntity<CustomResponse<List<SimpleStore>>> selectScrapedStore(@RequestHeader("Authorization") Optional<String> auth) {
        CustomResponse<List<SimpleStore>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<StoreInfo> storeInfos = storeService.selectScrapedStore(tokenInfo.get().getId());
            List<SimpleStore> stores = new ArrayList<>();
            for (StoreInfo storeInfo : storeInfos) {
                stores.add(storeInfo.convert2Dto());
            }
            res.setData(Optional.ofNullable(stores));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }


}
