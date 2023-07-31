package com.matsinger.barofishserver.user.api;


import com.matsinger.barofishserver.grade.domain.Grade;
import com.matsinger.barofishserver.jwt.*;
import com.matsinger.barofishserver.siteInfo.application.SiteInfoQueryService;
import com.matsinger.barofishserver.siteInfo.domain.SiteInformation;
import com.matsinger.barofishserver.user.application.UserCommandService;
import com.matsinger.barofishserver.user.application.UserQueryService;
import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.user.domain.UserOrderByAdmin;
import com.matsinger.barofishserver.user.domain.UserState;
import com.matsinger.barofishserver.user.dto.*;
import com.matsinger.barofishserver.user.paymentMethod.application.PaymentMethodCommandService;
import com.matsinger.barofishserver.user.paymentMethod.application.PaymentMethodQueryService;
import com.matsinger.barofishserver.user.paymentMethod.domain.PaymentMethod;
import com.matsinger.barofishserver.user.paymentMethod.dto.PaymentMethodDto;
import com.matsinger.barofishserver.userauth.application.UserAuthCommandService;
import com.matsinger.barofishserver.userauth.domain.LoginType;
import com.matsinger.barofishserver.userauth.domain.UserAuth;
import com.matsinger.barofishserver.userinfo.application.UserInfoCommandService;
import com.matsinger.barofishserver.userinfo.application.UserInfoQueryService;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.userinfo.dto.UserInfoDto;
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
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final UserInfoQueryService userInfoQueryService;
    private final UserInfoCommandService userInfoCommandService;
    private final UserAuthCommandService userAuthCommandService;
    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final PaymentMethodQueryService paymentMethodQueryService;
    private final PaymentMethodCommandService paymentMethodCommandService;
    private final S3Uploader s3;
    private final RegexConstructor re;
    private final VerificationService verificationService;
    private final JwtProvider jwtProvider;
    private final JwtService jwt;
    private final SmsService smsService;
    private final FcmTokenRepository fcmTokenRepository;
    private final SiteInfoQueryService siteInfoService;
    private final Common utils;
    @Value("${cloud.aws.s3.imageUrl}")
    private String s3Url;

    @PostMapping(value = "/join-sns")
    public ResponseEntity<CustomResponse<Jwt>> joinSnsUser(@RequestPart(value = "data") SnsJoinReq data) {
        CustomResponse<Jwt> res = new CustomResponse<>();
        try {
            if (data.getLoginType() == null) return res.throwError("로그인 타입을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (data.getLoginId() == null) return res.throwError("로그인 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Optional<UserAuth> userAuth = userCommandService.checkUserAuthExist(data.getLoginType(), data.getLoginId());
            if (userAuth.isPresent()) {
                User user = userCommandService.selectUser(userAuth.get().getUserId());
                if (user.getState().equals(UserState.BANNED)) return res.throwError("정지된 사용자입니다.", "NOT_ALLOWED");
                if (user.getState().equals(UserState.DELETED)) {
//                    return res.throwError("삭제된 사용자입니다.", "NOT_ALLOWED");
                    user.setState(UserState.ACTIVE);
                    userCommandService.updateUser(user);
                }
            } else {
                Grade grade = userCommandService.selectGrade(1);
                String profileImage = "";
                String phone = null;
                if (data.getPhone() != null && Pattern.matches(re.phone, data.getPhone())) {
//                    if (!Pattern.matches(re.phone, data.getPhone()))
//                        return res.throwError("휴대본 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                    phone = data.getPhone().replaceAll(re.getPhone(), "0$1$2$3");
                    if (userCommandService.checkExistWithPhone(phone))
                        return res.throwError("회원가입된 이력이 있습니다.", "NOT_ALLOWED");
                }
                SiteInformation siteInfo = siteInfoService.selectSiteInfo("INT_JOIN_POINT");
                Integer point = Integer.parseInt(siteInfo.getContent());
                User user = User.builder().joinAt(utils.now()).state(UserState.ACTIVE).build();
                UserInfo
                        userInfo =
                        UserInfo.builder().userId(user.getId()).nickname(data.getNickname() !=
                                null ? data.getNickname() : "").email(data.getEmail() !=
                                null ? data.getEmail() : "").profileImage(s3Url +
                                "/default_profile.png").name(data.getName() !=
                                null ? data.getName() : "").grade(grade).phone(phone).point(point).isAgreeMarketing(
                                false).build();
                UserAuth
                        newUserAuth =
                        UserAuth.builder().userId(user.getId()).loginId(data.getLoginId()).loginType(data.getLoginType()).build();
                UserInfo newUserInfo = userCommandService.addUser(user, newUserAuth, userInfo);
                if (data.getProfileImage() != null) {
                    profileImage =
                            s3.upload(s3.extractBase64FromImageUrl(data.getProfileImage()),
                                    new ArrayList<>(Arrays.asList("user", String.valueOf(user.getId()))));
                    newUserInfo.setProfileImage(profileImage);
                }
                if (data.getProfileImage() != null) {
                    profileImage =
                            s3.upload(s3.extractBase64FromImageUrl(data.getProfileImage()),
                                    new ArrayList<>(Arrays.asList("user", String.valueOf(user.getId()))));
                    newUserInfo.setProfileImage(profileImage);
                    userCommandService.addUserInfo(newUserInfo);
                }
                userAuth = Optional.ofNullable(newUserAuth);
            }
            User user = userCommandService.selectUser(userAuth.get().getUserId());
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

    @PostMapping(value = "/join", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CustomResponse<Boolean>> joinUser(@RequestPart(value = "data") UserJoinReq request,
                                                            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        try {
            verificationService.verifyPhoneVerification(request); // 휴대폰 번호 검증

            int userId = userCommandService.createIdPwUserAndSave(request);

            ArrayList<String> directoryElement = new ArrayList<String>();
            directoryElement.add("user");
            directoryElement.add(String.valueOf(userId));
            if (profileImage != null && !profileImage.isEmpty() && !s3.validateImageType(profileImage)) {
                return res.throwError("지원하지 않는 이미지 확장자입니다.", "INPUT_CHECK_REQUIRED");
            }
            String
                    imageUrl =
                    profileImage != null && !profileImage.isEmpty() ? s3.upload(profileImage,
                            directoryElement) : String.join("/", Arrays.asList(s3.getS3Url(), "default_profile.png"));
            userInfoCommandService.setImageUrl(userId, imageUrl);

            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
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
            UserInfo userInfo = userCommandService.selectUserInfo(userId);
            if (profileImage != null) {
                String
                        imageUrl =
                        s3.upload(profileImage,
                                new ArrayList<>(Arrays.asList("user", String.valueOf(userInfo.getUserId()))));
                userInfo.setProfileImage(imageUrl);
                userCommandService.updateUserInfo(userInfo);
            }
            if (data.getIsAgreeMarketing() != null) {
                userInfo.setIsAgreeMarketing(data.getIsAgreeMarketing());
                userCommandService.updateUserInfo(userInfo);
            }
            if (data.getName() != null) {
                String name = utils.validateString(data.getName(), 20L, "이름");
                userInfo.setName(name);
                userCommandService.updateUserInfo(userInfo);
            }
            if (data.getNickname() != null) {
                String nickname = utils.validateString(data.getNickname(), 50L, "닉네임");
                userInfo.setNickname(nickname);
                userCommandService.updateUserInfo(userInfo);
            }
            if (data.getNewPassword() != null) {
                if (data.getOldPassword() == null) return res.throwError("변경 전 비밀번호를 입력해주세요.", "INPUT_CHECK_REQUIRED");
                UserAuth userAuth = userCommandService.selectUserAuth(userId);
                if (!userAuth.getLoginType().equals(LoginType.IDPW))
                    return res.throwError("소셜 로그인 유저입니다.", "NOT_ALLOWED");
                if (!BCrypt.checkpw(data.getOldPassword(), userAuth.getPassword()))
                    return res.throwError("이전 비밀번호와 일치하지 않습니다.", "NOT_ALLOWED");
                if (!Pattern.matches(re.password, data.getNewPassword()))
                    return res.throwError("비밀번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                String password = BCrypt.hashpw(data.getNewPassword(), BCrypt.gensalt());
                userAuth.setPassword(password);
                userCommandService.updateUserPassword(userAuth);
            }
            if (data.getPhone() != null) {
                if (data.getVerificationId() == null) return res.throwError("인증 먼저 진행해주세요.", "INPUT_CHECK_REQUIRED");
                if (!Pattern.matches(re.phone, data.getPhone()))
                    return res.throwError("휴대폰 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                String phone = data.getPhone().replaceAll(re.getPhone(), "0$1$2$3");
                Verification verification = verificationService.selectVerificationById(data.getVerificationId());
                if (verification.getExpiredAt() != null) return res.throwError("인증을 먼저 진행해주세요.", "NOT_ALLOWED");
                if (userCommandService.checkExistWithPhone(phone) &&
                        (userInfo.getPhone() == null || !userInfo.getPhone().equals(phone)))
                    return res.throwError("이미 등록된 전화번호입니다.", "NOT_ALLOWED");
                userInfo.setPhone(phone);
                userCommandService.updateUserInfo(userInfo);
            }
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponse<Jwt>> loginUser(@RequestBody UserLoginReq request) {
        CustomResponse<Jwt> res = new CustomResponse<>();
        try {
            User findUser = userQueryService.login(request);

            String accessToken = jwtProvider.generateAccessToken(String.valueOf(findUser.getId()), TokenAuthType.USER);
            String
                    refreshToken =
                    jwtProvider.generateRefreshToken(String.valueOf(findUser.getId()), TokenAuthType.USER);
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
            UserInfoDto userInfoDto = userInfoQueryService.showMyPage(userId);

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
            UserInfoDto userInfoDto = userQueryService.manageByUserId(id);

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
            Page<UserInfoDto> userInfos = userQueryService.findPagedAndSpecificatedUserInfos(spec, pageRequest);
            res.setData(Optional.ofNullable(userInfos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/management/update-state")
    public ResponseEntity<CustomResponse<Boolean>> updateUserState(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                   @RequestPart(value = "data") UpdateUserStateReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer adminId = tokenInfo.get().getId();
            if (data.getState() == null) return res.throwError("상태를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            userCommandService.updateUserState(data.getUserIds(), data.getState(), adminId);

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
            userCommandService.withdrawUser(userId);

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<CustomResponse<Boolean>> updateFcm(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                             @RequestBody UpdateFcmReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            if (data.getFcmToken() == null) return res.throwError("토큰을 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Optional<FcmToken> token = fcmTokenRepository.findById(data.getFcmToken());
            if (data.getSet() == true) {
                if (token.isPresent()) {
                    token.get().setToken(data.getFcmToken());
                    fcmTokenRepository.save(token.get());
                } else {
                    fcmTokenRepository.save(FcmToken.builder().token(data.getFcmToken()).userId(userId).build());
                }
            } else {
                if (token.isPresent()) {
                    fcmTokenRepository.deleteById(data.getFcmToken());
                }
            }
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<CustomResponse<Boolean>> resetPassword(@RequestPart(value = "data") ResetPasswordReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        try {
            if (data.getVerificationId() == null) return res.throwError("인증 먼저 진행해주세요.", "INPUT_CHECK_REQUIRED");
            if (!Pattern.matches(re.phone, data.getPhone()))
                return res.throwError("휴대폰 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String phone = data.getPhone().replaceAll(re.getPhone(), "0$1$2$3");
            Verification verification = verificationService.selectVerificationById(data.getVerificationId());
            if (verification.getExpiredAt() != null) return res.throwError("인증을 먼저 진행해주세요.", "NOT_ALLOWED");

            String newPassword = userAuthCommandService.resetPassword(phone);
            smsService.sendSms(phone, "[바로피쉬]\n새로운 비밀번호는 " + newPassword + " 입니다.", null);

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/find-email")
    public ResponseEntity<CustomResponse<Boolean>> findEmail(@RequestPart(value = "data") FindEmailReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        try {
            if (data.getVerificationId() == null) return res.throwError("인증 먼저 진행해주세요.", "INPUT_CHECK_REQUIRED");
            if (!Pattern.matches(re.phone, data.getPhone()))
                return res.throwError("휴대폰 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String phone = data.getPhone().replaceAll(re.getPhone(), "0$1$2$3");
            Verification verification = verificationService.selectVerificationById(data.getVerificationId());
            if (verification.getExpiredAt() != null) return res.throwError("인증을 먼저 진행해주세요.", "NOT_ALLOWED");
            UserInfo userInfo = userCommandService.selectUserWithPhone(phone);
            UserAuth userAuth = userCommandService.selectUserAuth(userInfo.getUserId());
            if (!userAuth.getLoginType().equals(LoginType.IDPW)) return res.throwError("소셜 가입 사용자입니다.", "NOT_ALLOWED");
            smsService.sendSms(phone, String.format("[바로피쉬]\n회원님의 아이디는\n%s 입니다.", userAuth.getLoginId()), null);
            res.setData(Optional.of(true));
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
        if (tokenInfo.isEmpty()) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            if (tokenInfo.get().getType().equals(TokenAuthType.USER)) {
                userId = tokenInfo.get().getId();
            }
            if (userId == null) return res.throwError("유저 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");

            Optional<List<PaymentMethodDto>> paymentMethods = paymentMethodQueryService.getPaymentMethods(userId);
            res.setData(paymentMethods);
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
        if (tokenInfo.isEmpty()) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            PaymentMethod paymentMethod = paymentMethodQueryService.selectPaymentMethod(id);
            if (tokenInfo.get().getType().equals(TokenAuthType.USER) &&
                    paymentMethod.getUserId() != tokenInfo.get().getId())
                return res.throwError("타유저의 결제 수단입니다.", "NOT_ALLOWED");
            res.setData(Optional.ofNullable(paymentMethodQueryService.convert2Dto(paymentMethod)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/payment-method/add")
    public ResponseEntity<CustomResponse<PaymentMethodDto>> addPaymentMethod(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                             @RequestPart(value = "data") AddPaymentMethodReq data) {
        CustomResponse<PaymentMethodDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo.isEmpty()) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            PaymentMethod paymentMethod = paymentMethodCommandService.addPaymentMethod(data, tokenInfo.get().getId());

            res.setData(Optional.ofNullable(paymentMethodQueryService.convert2Dto(paymentMethod)));
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
            PaymentMethod paymentMethod = paymentMethodQueryService.selectPaymentMethod(id);
            if (paymentMethod.getUserId() != tokenInfo.get().getId())
                return res.throwError("타계정의 결제수단입니다.", "NOT_ALLOWED");
            paymentMethodQueryService.deletePaymentMethod(id);
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);

        }
    }
}