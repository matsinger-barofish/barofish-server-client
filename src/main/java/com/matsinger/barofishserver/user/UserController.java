package com.matsinger.barofishserver.user;


import com.matsinger.barofishserver.basketProduct.BasketService;
import com.matsinger.barofishserver.basketProduct.obejct.BasketProductDto;
import com.matsinger.barofishserver.grade.Grade;
import com.matsinger.barofishserver.inquiry.InquiryService;
import com.matsinger.barofishserver.jwt.*;
import com.matsinger.barofishserver.notification.NotificationService;
import com.matsinger.barofishserver.payment.PaymentService;
import com.matsinger.barofishserver.review.ReviewService;
import com.matsinger.barofishserver.siteInfo.SiteInfoService;
import com.matsinger.barofishserver.siteInfo.SiteInformation;
import com.matsinger.barofishserver.user.object.*;
import com.matsinger.barofishserver.userauth.LoginType;
import com.matsinger.barofishserver.userauth.UserAuth;
import com.matsinger.barofishserver.utils.AES256;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.RegexConstructor;
import com.matsinger.barofishserver.utils.S3.S3Uploader;
import com.matsinger.barofishserver.utils.fcm.FcmToken;
import com.matsinger.barofishserver.utils.fcm.FcmTokenRepository;
import com.matsinger.barofishserver.utils.sms.SmsService;
import com.matsinger.barofishserver.verification.Verification;
import com.matsinger.barofishserver.verification.VerificationService;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;
    private final S3Uploader s3;
    private final SiteInfoService siteInfoService;

    private final Common util;
    private final RegexConstructor re;
    private final VerificationService verificationService;
    private final JwtProvider jwtProvider;
    private final BasketService basketService;
    private final ReviewService reviewService;
    private final InquiryService inquiryService;
    private final NotificationService notificationService;
    private final FcmTokenRepository fcmTokenRepository;
    private final PaymentMethodService paymentMethodService;
    private final PaymentService paymentService;

    private final JwtService jwt;
    private final AES256 aes256;
    private final SmsService smsService;

    @GetMapping("/test")
    public ResponseEntity<CustomResponse<Page<UserInfoDto>>> test() {
        CustomResponse<Page<UserInfoDto>> res = new CustomResponse<>();
        try {
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
        String profileImage;
        String email;
        String name;
        String nickname;
        String phone;
    }

    @PostMapping(value = "/join-sns")
    public ResponseEntity<CustomResponse<Jwt>> joinSnsUser(@RequestPart(value = "data") SnsJoinReq data) {
        CustomResponse<Jwt> res = new CustomResponse<>();
        try {
            if (data.getLoginType() == null) return res.throwError("로그인 타입을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.getLoginId() == null) return res.throwError("로그인 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Optional<UserAuth> userAuth = userService.checkUserAuthExist(data.getLoginType(), data.getLoginId());
            if (userAuth.isPresent()) {
                User user = userService.selectUser(userAuth.get().getUserId());
                if (user.getState().equals(UserState.BANNED)) return res.throwError("정지된 사용자입니다.", "NOT_ALLOWED");
                if (user.getState().equals(UserState.DELETED)) return res.throwError("삭제된 사용자입니다.", "NOT_ALLOWED");
            } else {
                User user = User.builder().joinAt(util.now()).state(UserState.ACTIVE).build();
                user = userService.createUser(user);
                Grade grade = userService.selectGrade(1);
                String profileImage = "";
                String phone = null;
                if (data.phone != null) {
                    if (!Pattern.matches(re.phone, data.phone))
                        return res.throwError("휴대본 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                    phone = data.phone.replaceAll(re.getPhone(), "0$1$2$3");
                    if (userService.checkExistWithPhone(phone)) return res.throwError("회원가입된 이력이 있습니다.", "NOT_ALLOWED");
                }
                if (data.profileImage != null) {
                    profileImage =
                            s3.upload(s3.extractBase64FromImageUrl(data.profileImage),
                                    new ArrayList<>(Arrays.asList("user", String.valueOf(user.getId()))));
                }
                SiteInformation siteInfo = siteInfoService.selectSiteInfo("INT_JOIN_POINT");
                Integer point = Integer.parseInt(siteInfo.getContent());
                UserInfo
                        userInfo =
                        UserInfo.builder().userId(user.getId()).nickname(data.nickname !=
                                null ? data.nickname : "").email(data.email != null ? data.email : "").profileImage(
                                profileImage).name(data.name != null ? data.name : "").grade(grade).phone(phone).point(
                                point).isAgreeMarketing(false).build();
                userService.addUserInfo(userInfo);
                UserAuth
                        newUserAuth =
                        UserAuth.builder().userId(user.getId()).loginId(data.getLoginId()).loginType(data.getLoginType()).build();
                userService.addUserAuth(newUserAuth);
                userAuth = Optional.ofNullable(newUserAuth);
            }
            User user = userService.selectUser(userAuth.get().getUserId());
            if (!user.getState().equals(UserState.ACTIVE)) {
                if (user.getState().equals(UserState.BANNED)) return res.throwError("정지된 유저입니다..", "NOT_ALLOWED");
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

    @Getter
    @NoArgsConstructor
    static class UserJoinReq {
        String email;
        String name;
        String nickname;
        String password;
        String phone;
        Integer verificationId;
        String impUid;
        String postalCode;
        String address;
        String addressDetail;
        Boolean isAgreeMarketing;
    }

    @PostMapping(value = "/join", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CustomResponse<Boolean>> joinUser(@RequestPart(value = "data") UserJoinReq data,
                                                            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        try {
            if (profileImage != null && !profileImage.isEmpty() && !s3.validateImageType(profileImage))
                return res.throwError("지원하지 않는 이미지 확장자입니다.", "INPUT_CHECK_REQUIRED");
            String email = util.validateString(data.getEmail(), 300L, "이메일");
            if (!Pattern.matches(re.email, email)) return res.throwError("이메일 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String nickname = util.validateString(data.getNickname(), 50L, "닉네임");
            if (userService.checkExistWithNickname(nickname)) return res.throwError("중복된 닉네임입니다.", "NOT_ALLOWED");
            String name = util.validateString(data.getName(), 20L, "이름");
            if (data.postalCode == null) return res.throwError("우편 번호를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (!Pattern.matches(re.password, data.password))
                return res.throwError("비밀번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String password = BCrypt.hashpw(data.getPassword(), BCrypt.gensalt());
            if (!Pattern.matches(re.phone, data.phone))
                return res.throwError("휴대본 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String phone = data.phone.replaceAll(re.getPhone(), "0$1$2$3");
            if (userService.checkExistWithPhone(phone)) return res.throwError("회원가입된 이력이 있습니다.", "NOT_ALLOWED");
            Verification verification = null;
            if (data.verificationId == null && data.impUid == null)
                return res.throwError("인증을 먼저 진행해주세요.", "NOT_ALLOWED");
            else if (data.verificationId != null) {
                verification = verificationService.selectVerificationById(data.getVerificationId());
                if (verification.getExpiredAt() != null) return res.throwError("인증을 먼저 진행해주세요.", "NOT_ALLOWED");
            } else if (data.impUid != null) {
                verification = verificationService.selectVerificationByImpUid(data.impUid);
                if (verification.getExpiredAt() != null) return res.throwError("인증을 먼저 진행해주세요.", "NOT_ALLOWED");
            }
            String address = util.validateString(data.getAddress(), 100L, "주소");
            String addressDetail = util.validateString(data.getAddressDetail(), 100L, "상세 주소");
            Boolean isAgreeMarketing = data.getIsAgreeMarketing() != null && data.getIsAgreeMarketing();
            User user = User.builder().state(UserState.ACTIVE).joinAt(util.now()).build();
            String
                    imageUrl =
                    profileImage != null && !profileImage.isEmpty() ? s3.upload(profileImage,
                            new ArrayList<>(Arrays.asList("user", String.valueOf(user.getId())))) : String.join("/",
                            Arrays.asList(s3.getS3Url(), "default_profile.png"));

            UserAuth userAuth = UserAuth.builder().loginType(LoginType.IDPW).loginId(email).password(password).build();
            Grade grade = userService.selectGrade(1);
            SiteInformation siteInfo = siteInfoService.selectSiteInfo("INT_JOIN_POINT");
            Integer point = Integer.parseInt(siteInfo.getContent());
            UserInfo
                    userInfo =
                    UserInfo.builder().phone(phone).nickname(nickname).email(email).name(name).profileImage(imageUrl).grade(
                            grade).point(point).isAgreeMarketing(isAgreeMarketing).build();
            DeliverPlace
                    deliverPlace =
                    DeliverPlace.builder().name(name).receiverName(name).tel(phone).address(address).addressDetail(
                            addressDetail).deliverMessage("").postalCode(data.postalCode).isDefault(true).build();
            userService.addUser(user, userAuth, userInfo, deliverPlace);
            if (verification != null) verificationService.deleteVerification(verification.getId());
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
        Boolean isAgreeMarketing;
    }

    @PostMapping("/mypage/update")
    public ResponseEntity<CustomResponse<UserInfoDto>> updateUser(@RequestHeader("Authorization") Optional<String> auth,
                                                                  @RequestPart(value = "data") UserUpdateReq data,
                                                                  @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        CustomResponse<UserInfoDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            UserInfo userInfo = userService.selectUserInfo(userId);
            if (profileImage != null) {
                String
                        imageUrl =
                        s3.upload(profileImage,
                                new ArrayList<>(Arrays.asList("user", String.valueOf(userInfo.getUserId()))));
                userInfo.setProfileImage(imageUrl);
                userService.updateUserInfo(userInfo);
            }
            if (data.isAgreeMarketing != null) {
                userInfo.setIsAgreeMarketing(data.isAgreeMarketing);
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
                if (!BCrypt.checkpw(data.getOldPassword(), userAuth.getPassword()))
                    return res.throwError("이전 비밀번호와 일치하지 않습니다.", "NOT_ALLOWED");
                if (!Pattern.matches(re.password, data.getNewPassword()))
                    return res.throwError("비밀번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                String password = BCrypt.hashpw(data.getNewPassword(), BCrypt.gensalt());
                userAuth.setPassword(password);
                userService.updateUserPassword(userAuth);
            }
            if (data.getPhone() != null) {
                if (data.getVerificationId() == null) return res.throwError("인증 먼저 진행해주세요.", "INPUT_CHECK_REQUIRED");
                if (!Pattern.matches(re.phone, data.phone))
                    return res.throwError("휴대폰 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                String phone = data.phone.replaceAll(re.getPhone(), "0$1$2$3");
                Verification verification = verificationService.selectVerificationById(data.getVerificationId());
                if (verification.getExpiredAt() != null) return res.throwError("인증을 먼저 진행해주세요.", "NOT_ALLOWED");
                if (userService.checkExistWithPhone(phone) && !userInfo.getPhone().equals(phone))
                    return res.throwError("이미 등록된 전화번호입니다.", "NOT_ALLOWED");
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
    public ResponseEntity<CustomResponse<UserInfoDto>> selectUserSelfInfo(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<UserInfoDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            UserInfo userInfo = userService.selectUserInfo(userId);
            List<DeliverPlace> deliverPlaces = userService.selectUserDeliverPlaceList(userId);
            UserInfoDto
                    userInfoDto =
                    UserInfoDto.builder().userId(userId).profileImage(userInfo.getProfileImage()).grade(userInfo.getGrade()).email(
                            userInfo.getEmail()).name(userInfo.getName()).nickname(userInfo.getNickname()).phone(
                            userInfo.getPhone()).isAgreeMarketing(userInfo.getIsAgreeMarketing()).reviewCount(
                            reviewService.countAllReviewByUserId(userId)).notificationCount(notificationService.countAllNotificationByUserId(
                            userId)).deliverPlaces(deliverPlaces).point(userInfo.getPoint()).build();
            res.setData(Optional.ofNullable(userInfoDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/management/{id}")
    public ResponseEntity<CustomResponse<UserInfoDto>> selectUserList(@RequestHeader("Authorization") Optional<String> auth,
                                                                      @PathVariable("id") Integer id) {
        CustomResponse<UserInfoDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            UserInfo user = userService.selectUserInfo(id);
            UserInfoDto userInfoDto = user.convert2Dto();
            userInfoDto.setUser(userService.selectUser(user.getUserId()).convert2Dto());
            UserAuth userAuth = userService.selectUserAuth(user.getUserId());
            userAuth.setPassword(null);
            userInfoDto.setAuth(userAuth.convert2Dto());
            List<DeliverPlace> deliverPlaces = userService.selectUserDeliverPlaceList(user.getUserId());
            userInfoDto.setDeliverPlaces(deliverPlaces);
            res.setData(Optional.of(userInfoDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/management")
    public ResponseEntity<CustomResponse<Page<UserInfoDto>>> selectUserList(@RequestHeader("Authorization") Optional<String> auth,
                                                                            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                            @RequestParam(value = "take", required = false, defaultValue = "10") Integer take,
                                                                            @RequestParam(value = "orderby", defaultValue = "joinAt") UserOrderByAdmin orderBy,
                                                                            @RequestParam(value = "orderType", defaultValue = "DESC") Sort.Direction sort,
                                                                            @RequestParam(value = "name", required = false) String name,
                                                                            @RequestParam(value = "nickname", required = false) String nickname,
                                                                            @RequestParam(value = "email", required = false) String email,
                                                                            @RequestParam(value = "phone", required = false) String phone,
                                                                            @RequestParam(value = "state", required = false) String state,
                                                                            @RequestParam(value = "loginType", required = false) String loginType,
                                                                            @RequestParam(value = "joinAtS", required = false) Timestamp joinAtS,
                                                                            @RequestParam(value = "joinAtE", required = false) Timestamp joinAtE) {
        CustomResponse<Page<UserInfoDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Specification<UserInfo> spec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (name != null) predicates.add(builder.like(root.get("name"), "%" + name + "%"));
                if (nickname != null) predicates.add(builder.like(root.get("nickname"), "%" + nickname + "%"));
                if (email != null) predicates.add(builder.like(root.get("email"), "%" + email + "%"));
                if (phone != null) predicates.add(builder.like(root.get("phone"), "%" + phone + "%"));
                if (state != null)
                    predicates.add(builder.and(root.get("user").get("state").in(Arrays.stream(state.split(",")).map(
                            UserState::valueOf).toList())));
                if (joinAtS != null) predicates.add(builder.greaterThan(root.get("user").get("joinAt"), joinAtS));
                if (joinAtE != null) predicates.add(builder.lessThan(root.get("user").get("joinAt"), joinAtE));
                return builder.and(predicates.toArray(new Predicate[0]));
            };

            PageRequest pageRequest = PageRequest.of(page, take, Sort.by(sort, orderBy.label));
            Page<UserInfoDto> users = userService.selectUserInfoList(pageRequest, spec);
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
            if (data.userIds == null) return res.throwError("사용자 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            List<User> users = userService.selectUserListWithIds(data.userIds);
            if (users.stream().anyMatch(v -> v.getState().equals(UserState.DELETED)))
                return res.throwError("삭제된 사용자는 상태 변경 불가능합니다.", "NOT_ALLOWED");
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
            userInfo.setPhone(null);
            userService.deleteUserAuth(userId);
            //장바구니 제거
            List<Integer>
                    basketIds =
                    basketService.selectBasketList(userId).stream().map(BasketProductDto::getId).toList();
            basketService.deleteBasket(basketIds);

            //리뷰 제거
            reviewService.deleteReviewsByUserId(userId);
            inquiryService.deleteInquiryByUserId(userId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class UpdateFcmReq {
        String fcmToken;
        Boolean set;
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<CustomResponse<Boolean>> updateFcm(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                             @RequestBody UpdateFcmReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            if (data.fcmToken == null) return res.throwError("토큰을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Optional<FcmToken> token = fcmTokenRepository.findById(data.fcmToken);
            if (data.set) {
                if (token.isPresent()) {
                    token.get().setToken(data.fcmToken);
                    fcmTokenRepository.save(token.get());
                } else {
                    fcmTokenRepository.save(FcmToken.builder().token(data.fcmToken).userId(userId).build());
                }
            } else {
                if (token.isPresent()) {
                    fcmTokenRepository.deleteById(data.fcmToken);
                }
            }
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class ResetPasswordReq {
        String phone;
        Integer verificationId;
    }

    @PostMapping("/reset-password")
    public ResponseEntity<CustomResponse<Boolean>> resetPassword(@RequestPart(value = "data") ResetPasswordReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        try {
            if (data.getVerificationId() == null) return res.throwError("인증 먼저 진행해주세요.", "INPUT_CHECK_REQUIRED");
            if (!Pattern.matches(re.phone, data.phone))
                return res.throwError("휴대폰 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String phone = data.phone.replaceAll(re.getPhone(), "0$1$2$3");
            Verification verification = verificationService.selectVerificationById(data.getVerificationId());
            if (verification.getExpiredAt() != null) return res.throwError("인증을 먼저 진행해주세요.", "NOT_ALLOWED");
            UserInfo userInfo = userService.selectUserWithPhone(phone);
            if (userInfo == null) return res.throwError("소셜 로그인 유저입니다.", "NOT_ALLOWED");
            else {
                UserAuth userAuth = userService.selectUserAuth(userInfo.getUserId());
                if (!userAuth.getLoginType().equals(LoginType.IDPW))
                    return res.throwError("소셜 로그인 유저입니다.", "NOT_ALLOWED");
                String newPassword = userService.generateRandomString(6);
                userAuth.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                smsService.sendSms(phone, "[바로피쉬]\n새로운 비밀번호는 " + newPassword + " 입니다.", null);
                userService.updateUserPassword(userAuth);
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    // ---------------------------결제수단 관리------------------------------------
    @GetMapping("/payment-method")
    public ResponseEntity<CustomResponse<List<PaymentMethodDto>>> selectPaymentMethodList(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                          @RequestParam(value = "userId", required = false) Integer userId) {
        CustomResponse<List<PaymentMethodDto>> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (tokenInfo.get().getType().equals(TokenAuthType.USER)) {
                userId = tokenInfo.get().getId();
            } else {
                if (userId == null) return res.throwError("유저 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            }
            User user = userService.selectUser(userId);
            List<PaymentMethod> paymentMethods = paymentMethodService.selectPaymentMethodList(userId);
            res.setData(Optional.of(paymentMethods.stream().map(v -> {
                try {
                    return paymentMethodService.convert2Dto(v);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).toList()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/payment-method/{id}")
    public ResponseEntity<CustomResponse<PaymentMethodDto>> selectPaymentMethod(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                @PathVariable("id") Integer id) {
        CustomResponse<PaymentMethodDto> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER, TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            PaymentMethod paymentMethod = paymentMethodService.selectPaymentMethod(id);
            if (tokenInfo.get().getType().equals(TokenAuthType.USER) &&
                    paymentMethod.getUserId() != tokenInfo.get().getId())
                return res.throwError("타유저의 결제 수단입니다.", "NOT_ALLOWED");
            res.setData(Optional.ofNullable(paymentMethodService.convert2Dto(paymentMethod)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class AddPaymentMethodReq {
        String name;
        String cardNo;
        String expiryAt;
        String birth;
        String passwordTwoDigit;
    }

    @PostMapping("/payment-method/add")
    public ResponseEntity<CustomResponse<PaymentMethodDto>> addPaymentMethod(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                             @RequestPart(value = "data") AddPaymentMethodReq data) {
        CustomResponse<PaymentMethodDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            String name = util.validateString(data.name, 20L, "이름");
            if (data.cardNo == null) return res.throwError("카드번호를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (!Pattern.matches(re.cardNo, data.cardNo))
                return res.throwError("카드번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String cardNo = data.cardNo.replaceAll(re.cardNo, "$1$2$3$4");
            if (paymentMethodService.checkExistPaymentWithCardNo(cardNo, tokenInfo.get().getId()))
                return res.throwError("이미 등록된 카드입니다.", "NOT_ALLOWED");
            if (data.expiryAt == null) return res.throwError("유효기간(월/년) 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (!Pattern.matches(re.expiryAt, data.expiryAt))
                return res.throwError("유효기간 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.birth == null) return res.throwError("생년월일을 입력해주세요", "INPUT_CHECK_REQUIRED");
            if (!Pattern.matches(re.birth, data.birth))
                return res.throwError("생년월일 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String birth = data.birth;
            if (data.passwordTwoDigit == null) return res.throwError("비밀번호 두자리를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (!Pattern.matches(re.cardPassword, data.passwordTwoDigit))
                return res.throwError("비밀번호는 숫자 2자리입니다.", "INPUT_CHECK_REQUIRED");
            String password2Digit = aes256.encrypt(data.passwordTwoDigit);
            PaymentMethod
                    paymentMethod =
                    PaymentMethod.builder().name(name).cardNo(aes256.encrypt(cardNo)).userId(tokenInfo.get().getId()).expiryAt(
                            data.expiryAt).birth(birth).passwordTwoDigit(password2Digit).build();
            PaymentService.CheckValidCardRes validCardRes = paymentService.checkValidCard(paymentMethod);
            if (validCardRes == null) return res.throwError("유효하지 않은 카드입니다.", "INPUT_CHECK_REQUIRED");
            paymentMethod.setCardName(validCardRes.getCardName());
            paymentMethod.setCustomerUid(validCardRes.getCustomerUid());
            paymentMethod = paymentMethodService.addPaymentMethod(paymentMethod);
            res.setData(Optional.ofNullable(paymentMethodService.convert2Dto(paymentMethod)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @DeleteMapping("payment-method/{id}")
    public ResponseEntity<CustomResponse<Boolean>> deletePaymentMethod(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                       @PathVariable("id") Integer id) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            PaymentMethod paymentMethod = paymentMethodService.selectPaymentMethod(id);
            if (paymentMethod.getUserId() != tokenInfo.get().getId())
                return res.throwError("타계정의 결제수단입니다.", "NOT_ALLOWED");
            paymentMethodService.deletePaymentMethod(id);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);

        }
    }
}
