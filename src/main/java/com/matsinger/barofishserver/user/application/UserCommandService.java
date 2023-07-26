package com.matsinger.barofishserver.user.application;

import com.matsinger.barofishserver.admin.log.application.AdminLogCommandService;
import com.matsinger.barofishserver.admin.log.application.AdminLogQueryService;
import com.matsinger.barofishserver.admin.log.domain.AdminLog;
import com.matsinger.barofishserver.admin.log.domain.AdminLogType;
import com.matsinger.barofishserver.basketProduct.application.BasketCommandService;
import com.matsinger.barofishserver.basketProduct.application.BasketQueryService;
import com.matsinger.barofishserver.basketProduct.dto.BasketProductDto;
import com.matsinger.barofishserver.grade.domain.Grade;
import com.matsinger.barofishserver.grade.repository.GradeRepository;
import com.matsinger.barofishserver.inquiry.application.InquiryCommandService;
import com.matsinger.barofishserver.review.application.ReviewCommandService;
import com.matsinger.barofishserver.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.user.deliverplace.repository.DeliverPlaceRepository;
import com.matsinger.barofishserver.user.dto.SnsJoinLoginResponseDto;
import com.matsinger.barofishserver.user.dto.UserJoinReq;
import com.matsinger.barofishserver.user.repository.UserRepository;
import com.matsinger.barofishserver.user.dto.SnsJoinReq;
import com.matsinger.barofishserver.user.domain.*;
import com.matsinger.barofishserver.user.exception.UserException;
import com.matsinger.barofishserver.userauth.application.UserAuthCommandService;
import com.matsinger.barofishserver.userauth.domain.LoginType;
import com.matsinger.barofishserver.userauth.domain.UserAuth;
import com.matsinger.barofishserver.userauth.repository.UserAuthRepository;
import com.matsinger.barofishserver.userinfo.application.UserInfoCommandService;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.userinfo.dto.UserInfoDto;
import com.matsinger.barofishserver.userinfo.repository.UserInfoRepository;
import com.matsinger.barofishserver.utils.Common;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCommandService {
    private final UserRepository userRepository;

    private final UserInfoRepository userInfoRepository;
    private final UserAuthRepository userAuthRepository;
    private final DeliverPlaceRepository deliverPlaceRepository;
    private final GradeRepository gradeRepository;
    private final BasketQueryService basketQueryService;
    private final BasketCommandService basketCommandService;
    private final UserException userJoinValidator;
    private final ReviewCommandService reviewService;
    private final InquiryCommandService inquiryCommandService;
    private final UserAuthCommandService userAuthCommandService;
    private final UserInfoCommandService userInfoCommandService;
    private final Common util;
    private final AdminLogCommandService adminLogCommandService;
    private final AdminLogQueryService adminLogQueryService;

    @Transactional
    public SnsJoinLoginResponseDto createSnsUserAndSave(SnsJoinReq request) throws MalformedURLException {

        userJoinValidator.nullCheck(request.getLoginType());
        userJoinValidator.nullCheck(request.getLoginId());

        User user = User.builder().joinAt(new Timestamp(System.currentTimeMillis())).state(UserState.ACTIVE).build();
        userRepository.save(user);

        UserAuth createdUserAuth = userAuthCommandService.createUserAuth(request, user);
        Grade grade = gradeRepository.findById(1).orElseThrow(() -> new IllegalStateException("등급 정보를 찾을 수 없습니다."));
        userInfoCommandService.createAndSaveUserInfo(user, request, "", grade);

        return SnsJoinLoginResponseDto.builder()
                .userId(user.getId())
                .loginId(createdUserAuth.getLoginId())
                .build();
    }

    @Transactional
    public int createIdPwUserAndSave(UserJoinReq request) throws Exception {
        User
                createdUser =
                User.builder().state(UserState.ACTIVE).joinAt(new Timestamp(System.currentTimeMillis())).build();
        userRepository.save(createdUser);

        userAuthCommandService.createIdPwUserAuthAndSave(createdUser, request);
        Grade
                findGrade =
                gradeRepository.findById(1).orElseThrow(() -> new IllegalArgumentException("등급 정보를 찾을 수 없습니다."));
        UserInfo createdUserInfo = userInfoCommandService.createAndSaveIdPwUserInfo(createdUser, request, findGrade);

        setAndSaveDeliverPlace(createdUser, createdUserInfo, request);
        return createdUser.getId();
    }

    public DeliverPlace setAndSaveDeliverPlace(User user, UserInfo userInfo, UserJoinReq request) throws Exception {

        String address = util.validateString(request.getAddress(), 100L, "주소");
        String addressDetail = util.validateString(request.getAddressDetail(), 100L, "상세 주소");

        if (request.getPostalCode() == null) {
            throw new IllegalArgumentException("우편 번호를 입력해주세요.");
        }

        DeliverPlace
                createdDeliver =
                DeliverPlace.builder().userId(user.getId()).name(userInfo.getName()).receiverName(userInfo.getName()).tel(
                        userInfo.getPhone()).address(address).addressDetail(addressDetail).deliverMessage("").postalCode(
                        request.getPostalCode()).isDefault(true).build();

        return deliverPlaceRepository.save(createdDeliver);
    }

    @Transactional
    public void deleteUserAuth(Integer userId) {
        userAuthRepository.deleteAllByUserId(userId);
    }

    @Transactional
    public void withdrawUser(int userId) {
        User findUser = userRepository.findById(userId).orElseThrow(() -> {
            throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
        });
        UserInfo findUserInfo = userInfoRepository.findByUserId(userId).orElseThrow(() -> {
            throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
        });
        findUser.setState(UserState.DELETED);
        findUserInfo.setNickname("탈퇴한 회원");
        findUserInfo.setPhone(null);
        deleteUserAuth(userId);

        // 장바구니 제거
        List<Integer>
                basketIds =
                basketQueryService.selectBasketList(userId).stream().map(BasketProductDto::getId).toList();
        basketCommandService.deleteBasket(basketIds);
        // 리뷰제거
        reviewService.deleteReviewsByUserId(userId);
        inquiryCommandService.deleteInquiryByUserId(userId);
    }

    public Page<UserInfoDto> selectUserInfoList(PageRequest pageRequest, Specification<UserInfo> spec) {
        return userInfoRepository.findAll(spec, pageRequest).map(userInfo -> {
            UserInfoDto userInfoDto = userInfo.convert2Dto();
            userInfoDto.setUser(selectUser(userInfo.getUserId()).convert2Dto());
            UserAuth userAuth = selectUserAuth(userInfo.getUserId());
            if (userAuth != null) {
                userAuth.setPassword(null);
                userInfoDto.setAuth(userAuth.convert2Dto());
            }
            List<DeliverPlace> deliverPlaces = selectUserDeliverPlaceList(userInfo.getUserId());
            userInfoDto.setDeliverPlaces(deliverPlaces);
            return userInfoDto;
        });
    }

    public DeliverPlace selectDeliverPlace(Integer id) {
        return deliverPlaceRepository.findById(id).orElseThrow(() -> {
            throw new Error("배송지 정보를 찾을 수 없습니다.");
        });
    }

    public Boolean checkExistWithPhone(String phone) {
        return userInfoRepository.existsByPhone(phone);
    }

    public Boolean checkExistWithNickname(String nickname) {
        return userInfoRepository.existsByNickname(nickname);
    }

    public Optional<User> selectUserOptional(Integer id) {
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<User> selectUserListWithIds(List<Integer> ids) {
        return userRepository.findAllByIdIn(ids);
    }

    public UserInfo selectUserInfo(Integer id) {
        return userInfoRepository.findById(id).orElseThrow(() -> {
            throw new Error("유저 정보를 찾을 수 없습니다.");
        });
    }

    public Optional<UserInfo> selectOptionalUserInfo(Integer id) {
        return userInfoRepository.findById(id);
    }

    public UserAuth selectUserAuth(Integer id) {
        return userAuthRepository.findFirstByUserId(id);
    }

    public List<User> selectUserList() {
        return userRepository.findAll();
    }

    public User selectUser(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> {
            throw new Error("유저 정보를 찾을 수 없습니다.");
        });
    }

    public User createSnsUserAndSave(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void addUser(User user, UserAuth userAuth, UserInfo userInfo, DeliverPlace deliverPlace) {
        User res = userRepository.save(user);
        userAuth.setUserId(res.getId());
        userInfo.setUserId(res.getId());
        userInfo.setUser(res);
        deliverPlace.setUserId(res.getId());
        userAuthRepository.save(userAuth);
        UserInfo result = userInfoRepository.save(userInfo);
        deliverPlaceRepository.save(deliverPlace);
    }

    @Transactional
    public UserInfo addUser(User user, UserAuth userAuth, UserInfo userInfo) {
        User res = userRepository.save(user);
        userAuth.setUserId(res.getId());
        userInfo.setUserId(res.getId());
        userInfo.setUser(res);
        userAuthRepository.save(userAuth);
        UserInfo result = userInfoRepository.save(userInfo);
        return result;
    }

    public UserAuth selectUserByLoginId(LoginType loginType, String loginId) {
        try {
            return userAuthRepository.findByLoginTypeAndLoginId(loginType, loginId).orElseThrow();
        } catch (Exception e) {
            return null;
        }
    }

    public Optional<UserAuth> checkUserAuthExist(LoginType loginType, String loginId) {
        return userAuthRepository.findByLoginTypeAndLoginId(loginType, loginId);
    }

    public List<DeliverPlace> selectUserDeliverPlaceList(Integer userId) {
        return deliverPlaceRepository.findAllByUserId(userId);
    }

    public UserAuth addUserAuth(UserAuth userAuth) {
        return userAuthRepository.save(userAuth);
    }

    public void addUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    public void updateUserState(List<Integer> userIds, UserState state, Integer adminId) {
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            user.setState(state);
            String
                    content =
                    String.format("%s -> %s 상태 변경하였습니다.",
                            user.getState().equals(UserState.ACTIVE) ? "활동중" : "정지",
                            state.equals(UserState.ACTIVE) ? "활동중" : "정지");
            AdminLog
                    adminLog =
                    AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.USER).targetId(
                            String.valueOf(user.getId())).content(content).createdAt(util.now()).build();
            adminLogCommandService.saveAdminLog(adminLog);
        }
        users = userRepository.saveAll(users);
    }

    public void updateUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    public void updateUserPassword(UserAuth userAuth) {
        userAuthRepository.save(userAuth);
    }

    public List<Grade> selectGradeList() {
        return gradeRepository.findAll();
    }

    public Grade selectGrade(Integer id) {
        return gradeRepository.findById(id).orElseThrow(() -> {
            throw new Error("등급 정보를 찾을 수 없습니다.");
        });
    }

    public void addPoint(UserInfo userInfo, Integer point) {
        userInfo.setPoint(userInfo.getPoint() + point);
        userInfoRepository.save(userInfo);
    }

    public List<User> selectUserWithState(UserState state) {
        return userRepository.findAllByState(state);
    }

    public UserInfo selectUserWithPhone(String phone) {
        return userInfoRepository.findByPhone(phone).orElseThrow(() -> {
            throw new IllegalArgumentException("휴대폰 번호를 찾을 수 없습니다.");
        });
    }

    public Optional<UserInfo> selectOptionalUserInfo(int userId) {
        return userInfoRepository.findByUserId(userId);
    }

    @Transactional
    public void addUserAuthIfPhoneNumberExists(SnsJoinReq request) {

        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByPhone(request.getPhone().replace("-", ""));

        boolean isUserInfoExists = optionalUserInfo.isPresent();

        boolean isLoginTypeExists = false;
        if (isUserInfoExists) {
            UserInfo findUserInfo = optionalUserInfo.get();
            List<UserAuth> existingUserAuth = findUserInfo.getUser().getUserAuth();

            for (UserAuth userAuth : existingUserAuth) {
                if (userAuth.getLoginType() == request.getLoginType()) {
                    isLoginTypeExists = true;
                }
            }
        }

        if (isUserInfoExists && !isLoginTypeExists) {
            UserAuth createdUserAuth = UserAuth.builder()
                    .loginType(request.getLoginType())
                    .loginId(request.getLoginId())
                    .build();
            User findUser = optionalUserInfo.get().getUser();
            createdUserAuth.setUserId(findUser.getId());
            createdUserAuth.setUser(findUser);

            userAuthRepository.save(createdUserAuth);
        }
    }

    @Transactional
    public boolean addUserAuthIfPhoneNumberExists(UserJoinReq request) throws Exception {

        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByPhone(request.getPhone().replace("-", ""));

        if (!optionalUserInfo.isPresent()) {
            return false;
        }

        UserInfo findUserInfo = optionalUserInfo.get();
        List<UserAuth> existingUserAuth = findUserInfo.getUser().getUserAuth();

        for (UserAuth userAuth : existingUserAuth) {
            if (userAuth.getLoginType() == LoginType.IDPW) {
                throw new IllegalArgumentException("회원가입된 이력이 있습니다");
            }
        }

        userAuthCommandService.createIdPwUserAuthAndSave(findUserInfo.getUser(), request);

        return true;
    }
}
