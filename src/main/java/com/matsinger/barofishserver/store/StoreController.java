package com.matsinger.barofishserver.store;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import lombok.RequiredArgsConstructor;
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
    private final JwtService jwt;

    private final Common utils;
    private final S3Uploader s3;

    @GetMapping("")
    public ResponseEntity<CustomResponse<List<Store>>> selectStoreList(@RequestHeader(value = "Authorization", required = false) Optional<String> auth) {
        CustomResponse<List<Store>> res = new CustomResponse<>();
        try {
            Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
            Boolean
                    isAdmin =
                    tokenInfo != null && tokenInfo.isPresent() && tokenInfo.get().getType().equals(TokenAuthType.ADMIN);
            List<Store> stores = storeService.selectStoreList(isAdmin);
            for (Store data : stores) {
                data.setPassword(null);
            }
            res.setData(Optional.ofNullable(stores));

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<StoreInfo>> selectStore(@PathVariable("id") Integer id) {
        CustomResponse<StoreInfo> res = new CustomResponse<>();
        try {
            StoreInfo storeInfo = storeService.selectStore(id);
            res.setData(Optional.ofNullable(storeInfo));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Store>> addStore(@RequestHeader("Authorization") Optional<String> auth,
                                                          @RequestPart(value = "loginId") String loginId,
                                                          @RequestPart(value = "password") String password,
                                                          @RequestPart(value = "backgroundImage") MultipartFile backgroundImage,
                                                          @RequestPart(value = "profileImage") MultipartFile profileImage,
                                                          @RequestPart(value = "name") String name,
                                                          @RequestPart(value = "location") String location,
                                                          @RequestPart(value = "keyword") String keyword) {
        CustomResponse<Store> res = new CustomResponse<>();
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
            Store store = storeService.addStore(storeData);
            String
                    backgroundImageUrl =
                    s3.upload(backgroundImage, new ArrayList<>(Arrays.asList("store", String.valueOf(store.getId()))));
            String
                    profileImageUrl =
                    s3.upload(profileImage, new ArrayList<>(Arrays.asList("store", String.valueOf(store.getId()))));
            storeInfoData.setBackgroudImage(backgroundImageUrl);
            storeInfoData.setProfileImage(profileImageUrl);
            StoreInfo storeInfo = storeService.addStoreInfo(storeInfoData);
            store.setStoreInfo(storeInfo);
            res.setData(Optional.of(store));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }


}
