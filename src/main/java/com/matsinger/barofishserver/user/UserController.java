package com.matsinger.barofishserver.user;


import com.matsinger.barofishserver.admin.AdminState;
import com.matsinger.barofishserver.basketProduct.BasketService;
import com.matsinger.barofishserver.inquiry.InquiryService;
import com.matsinger.barofishserver.jwt.*;
import com.matsinger.barofishserver.review.ReviewService;
import com.matsinger.barofishserver.user.object.*;
import com.matsinger.barofishserver.userauth.LoginType;
import com.matsinger.barofishserver.userauth.UserAuth;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.RegexConstructor;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import com.matsinger.barofishserver.verification.Verification;
import com.matsinger.barofishserver.verification.VerificationService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;


    private final S3Uploader s3;

    private final Common util;
    private final RegexConstructor re;
    private final VerificationService verificationService;
    private final JwtProvider jwtProvider;
    private final BasketService basketService;
    private final ReviewService reviewService;
    private final InquiryService inquiryService;
    private final JwtService jwt;


    @Getter
    @NoArgsConstructor
    private static class TestClass {
        String data;
    }

    @GetMapping("/test")
    public ResponseEntity<CustomResponse<Boolean>> test(@RequestBody TestClass data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        try {
            s3.uploadEditorStringToS3(data.data, new ArrayList<>(Arrays.asList("tmp")));
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class SnsJoinReq {
        LoginType loginType;
        String loginId;
    }

    @PostMapping(value = "/join-sns")
    public ResponseEntity<CustomResponse<Jwt>> joinSnsUser(@RequestPart(value = "data") SnsJoinReq data) {
        CustomResponse<Jwt> res = new CustomResponse<>();
        try {
            if (data.getLoginType() == null) return res.throwError("로그인 타입을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.getLoginId() == null) return res.throwError("로그인 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            System.out.println(data.getLoginId() + data.getLoginType());
            Optional<UserAuth> userAuth = userService.checkUserAuthExist(data.getLoginType(), data.getLoginId());
            if (userAuth.isPresent()) {

            } else {
                User user = User.builder().joinAt(util.now()).state(UserState.ACTIVE).build();
                user = userService.createUser(user);
                Grade grade = userService.selectGrade(1);
                UserInfo
                        userInfo =
                        UserInfo.builder().userId(user.getId()).nickname("").email("").profileImage("").name("").grade(
                                grade).phone("").point(0).isAgreeMarketing(false).build();
                userService.addUserInfo(userInfo);
                UserAuth
                        newUserAuth =
                        userService.addUserAuth(UserAuth.builder().userId(user.getId()).loginId(data.getLoginId()).loginType(
                                data.getLoginType()).build());
                userAuth = Optional.ofNullable(newUserAuth);
            }
            User user = userService.selectUser(userAuth.get().getUserId());
            if (!user.getState().equals(AdminState.ACTIVE)) {
                if (user.getState().equals(AdminState.BANNED)) return res.throwError("정지된 유저입니다..", "NOT_ALLOWED");
                if (user.getState().equals(AdminState.DELETED)) return res.throwError("삭제된 유저입니다.", "NOT_ALLOWED");
            }
            String accessToken = jwtProvider.generateAccessToken(String.valueOf(user.getId()), TokenAuthType.USER);
            String refreshToken = jwtProvider.generateRefreshToken(String.valueOf(user.getId()), TokenAuthType.USER);
            Jwt token = new Jwt();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            res.setData(Optional.of(token));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    static class UserJoinReq {
        String email;
        String name;
        String nickname;
        String password;
        String phone;
        Integer verificationId;
        String postalCode;
        String address;
        String addressDetail;
        Boolean isAgreeMarketing;
    }

    @PostMapping(value = "/join", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CustomResponse<Boolean>> joinUser(@RequestPart(value = "data") UserJoinReq data,
                                                            @RequestPart(value = "profileImage") MultipartFile profileImage) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        try {
            if (!s3.validateImageType(profileImage))
                return res.throwError("지원하지 않는 이미지 확장자입니다.", "INPUT_CHECK_REQUIRED");
            String email = util.validateString(data.getEmail(), 300L, "이메일");
            if (!Pattern.matches(re.email, email)) return res.throwError("이메일 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String nickname = util.validateString(data.getNickname(), 50L, "닉네임");
            String name = util.validateString(data.getName(), 20L, "이름");
            if (data.postalCode == null) return res.throwError("우편 번호를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (!Pattern.matches(re.password, data.password))
                return res.throwError("비밀번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String password = BCrypt.hashpw(data.getPassword(), BCrypt.gensalt());
            if (!Pattern.matches(re.phone, data.phone))
                return res.throwError("휴대본 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String phone = data.phone.replaceAll(re.getPhone(), "0$1$2$3");
            Verification verification = verificationService.selectVerificationById(data.getVerificationId());
            if (verification.getExpiredAt() != null) return res.throwError("인증을 먼저 진행해주세요.", "NOT_ALLOWED");
            String address = util.validateString(data.getAddress(), 100L, "주소");
            String addressDetail = util.validateString(data.getAddressDetail(), 100L, "상세 주소");
            Boolean isAgreeMarketing = data.getIsAgreeMarketing() == null ? false : data.getIsAgreeMarketing();
            User user = User.builder().state(UserState.ACTIVE).joinAt(util.now()).build();
            String
                    imageUrl =
                    s3.upload(profileImage, new ArrayList<>(Arrays.asList("user", String.valueOf(user.getId()))));

            User userResult = userService.createUser(user);
            UserAuth
                    userAuth =
                    UserAuth.builder().userId(userResult.getId()).loginType(LoginType.IDPW).loginId(email).password(
                            password).build();
            Grade grade = userService.selectGrade(1);
            UserInfo
                    userInfo =
                    UserInfo.builder().userId(userResult.getId()).phone(phone).nickname(nickname).email(email).name(name).profileImage(
                            imageUrl).grade(grade).point(0).isAgreeMarketing(isAgreeMarketing).build();
            DeliverPlace
                    deliverPlace =
                    DeliverPlace.builder().userId(userResult.getId()).name(name).receiverName(name).tel(phone).address(
                            address).addressDetail(addressDetail).deliverMessage("").postalCode(data.postalCode).isDefault(
                            true).build();
            userService.addUser(user, userAuth, userInfo, deliverPlace);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    static class UserUpdateReq {
        String name;
        String nickname;
        String oldPassword;
        String newPassword;
        String phone;
        Integer verificationId;
        String address;
        String addressDetail;
    }

    @PostMapping("/mypage/update")
    public ResponseEntity<CustomResponse<UserDto>> updateUser(@RequestHeader("Authorization") Optional<String> auth,
                                                              @RequestPart(value = "data") UserUpdateReq data,
                                                              @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        CustomResponse<UserDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            UserInfo userInfo = userService.selectUserInfo(userId);
            if (profileImage != null) {
                String imageUrl = s3.upload(profileImage, new ArrayList<>(Arrays.asList("user", userInfo.toString())));
                userInfo.setProfileImage(imageUrl);
                userService.updateUserInfo(userInfo);
            }
            if (data.name != null) {
                String name = util.validateString(data.getName(), 20L, "이름");
                userInfo.setName(name);
                userService.updateUserInfo(userInfo);
            }
            if (data.getNickname() != null) {
                String nickname = util.validateString(data.getNickname(), 50L, "닉네임");
                userInfo.setNickname(nickname);
                userService.updateUserInfo(userInfo);
            }
            if (data.getNewPassword() != null) {
                if (data.getOldPassword() == null) return res.throwError("변경 전 비밀번호를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                UserAuth userAuth = userService.selectUserAuth(userId);
                if (!userAuth.getLoginType().equals(LoginType.IDPW))
                    return res.throwError("소셜 로그인 유저입니다.", "NOT_ALLOWED");
                if (!Pattern.matches(re.password, data.getNewPassword()))
                    return res.throwError("비밀번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                String password = BCrypt.hashpw(data.getNewPassword(), BCrypt.gensalt());
                userAuth.setPassword(password);
                userService.updateUerPassword(userAuth);
            }
            if (data.getPhone() != null) {
                if (data.getVerificationId() == null) return res.throwError("인증 먼저 진행해주세요.", "INPUT_CHECK_REQUIRED");
                if (!Pattern.matches(re.phone, data.phone))
                    return res.throwError("휴대폰 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                String phone = data.phone.replaceAll(re.getPhone(), "0$1$2$3");
                Verification verification = verificationService.selectVerificationById(data.getVerificationId());
                if (verification.getExpiredAt() != null) return res.throwError("인증을 먼저 진행해주세요.", "NOT_ALLOWED");
                userInfo.setPhone(phone);
                userService.updateUserInfo(userInfo);
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }

    }


    @Getter
    @NoArgsConstructor
    static class UserLoginReq {
        LoginType loginType;
        String loginId;
        String password;
    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponse<Jwt>> loginUser(@RequestBody UserLoginReq data) {
        CustomResponse<Jwt> res = new CustomResponse<>();
        try {
            UserAuth userAuth = userService.selectUserByLoginId(data.getLoginType(), data.getLoginId());
            if (userAuth == null) return res.throwError("아이디 및 비밀번호를 확인해주세요.", "INPUT_CHECK_REQUIRED");
            if (!BCrypt.checkpw(data.getPassword(), userAuth.getPassword()))
                return res.throwError("아이디 및 비밀번호를 확인해주세요.", "INPUT_CHECK_REQUIRED");
            User user = userService.selectUser(userAuth.getUserId());
            if (!user.getState().equals(UserState.ACTIVE)) {
                if (user.getState().equals(UserState.BANNED)) return res.throwError("정지된 유저입니다.", "NOT_ALLOWED");
                if (user.getState().equals(UserState.DELETED)) return res.throwError("삭제된 유저입니다.", "NOT_ALLOWED");
            }
            String accessToken = jwtProvider.generateAccessToken(String.valueOf(user.getId()), TokenAuthType.USER);
            String refreshToken = jwtProvider.generateRefreshToken(String.valueOf(user.getId()), TokenAuthType.USER);
            Jwt token = new Jwt();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            res.setData(Optional.of(token));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/mypage")
    public ResponseEntity<CustomResponse<UserDto>> selectUserSelfInfo(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<UserDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            UserInfo userInfo = userService.selectUserInfo(userId);
            List<DeliverPlace> deliverPlaces = userService.selectUserDeliverPlaceList(userId);
            UserDto
                    userDto =
                    UserDto.builder().userId(userId).profileImage(userInfo.getProfileImage()).email(userInfo.getEmail()).name(
                            userInfo.getName()).nickname(userInfo.getNickname()).phone(userInfo.getPhone()).isAgreeMarketing(
                            userInfo.getIsAgreeMarketing()).deliverPlaces(deliverPlaces).build();
            res.setData(Optional.ofNullable(userDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/management/{id}")
    public ResponseEntity<CustomResponse<UserDto>> selectUserList(@RequestHeader("Authorization") Optional<String> auth,
                                                                  @PathVariable("id") Integer id) {
        CustomResponse<UserDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            UserInfo user = userService.selectUserInfo(id);
            UserDto userDto = user.convert2Dto();
            userDto.setUser(userService.selectUser(user.getUserId()));
            UserAuth userAuth = userService.selectUserAuth(user.getUserId());
            userAuth.setPassword(null);
            userDto.setAuth(userAuth);
            List<DeliverPlace> deliverPlaces = userService.selectUserDeliverPlaceList(user.getUserId());
            userDto.setDeliverPlaces(deliverPlaces);
            res.setData(Optional.of(userDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/management")
    public ResponseEntity<CustomResponse<Page<UserDto>>> selectUserList(@RequestHeader("Authorization") Optional<String> auth,
                                                                        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                        @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<Page<UserDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Page<UserDto> users = userService.selectUserInfoList(page, take);
            res.setData(Optional.ofNullable(users));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class UpdateUserStateReq {
        List<Integer> userIds;
        UserState state;
    }

    @PostMapping("/management/update-state")
    public ResponseEntity<CustomResponse<Boolean>> updateUserState(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                   @RequestPart(value = "data") UpdateUserStateReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (data.state == null) return res.throwError("상태를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            userService.updateUserState(data.userIds, data.getState());
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/verifyToken")
    public ResponseEntity<CustomResponse<String>> whoami(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<String> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ALLOW), auth);
        try {
            if (tokenInfo != null && tokenInfo.isPresent())
                res.setData(Optional.of(("" + tokenInfo.get().getType() + tokenInfo.get().getId())));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<CustomResponse<Boolean>> withdrawUser(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            User user = userService.selectUser(userId);
            UserInfo userInfo = userService.selectUserInfo(userId);
            user.setState(UserState.DELETED);
            userInfo.setNickname("탈퇴한 회원");
            //장바구니 제거
            List<Integer> basketIds = basketService.selectBasketList(userId).stream().map(basketProductDto -> {
                return basketProductDto.getId();
            }).toList();
            basketService.deleteBasket(basketIds);

            //리뷰 제거
            reviewService.deleteReviewsByUserId(userId);
            inquiryService.deleteInquiryByUserId(userId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
