package com.matsinger.barofishserver.domain.userinfo.application;

import com.matsinger.barofishserver.domain.grade.domain.Grade;
import com.matsinger.barofishserver.domain.grade.repository.GradeRepository;
import com.matsinger.barofishserver.domain.user.domain.User;
import com.matsinger.barofishserver.domain.user.dto.SnsJoinReq;
import com.matsinger.barofishserver.domain.user.dto.UserJoinReq;
import com.matsinger.barofishserver.domain.user.dto.UserUpdateReq;
import com.matsinger.barofishserver.domain.user.repository.UserRepository;
import com.matsinger.barofishserver.domain.userauth.domain.LoginType;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.domain.userinfo.repository.UserInfoRepository;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.RegexConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserInfoCommandService {

    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final UserInfoRepository userInfoRepository;
    private final RegexConstructor re;
    private final CustomResponse customResponse;
    private final Common util;

    public UserInfo updateUserInfo(Integer userId, UserUpdateReq request, String imageUrl) throws Exception {

        User findUser = userRepository.findById(userId).orElseThrow(() -> {
            throw new IllegalStateException("유저 정보를 찾을 수 없습니다.");
        });
        UserInfo findUserInfo = userInfoRepository.findByUserId(findUser.getId()).orElseThrow(() -> {
            throw new IllegalArgumentException("유저 정보를 찾을 수 없습니다.");
        });

        if (imageUrl != null) {
            findUserInfo.setProfileImage(imageUrl);
        }
        if (request.getIsAgreeMarketing() != null) {
            findUserInfo.setIsAgreeMarketing(request.getIsAgreeMarketing());
        }
        if (request.getName() != null) {
            String name = util.validateString(request.getName(), 20L, "이름");
            findUserInfo.setName(name);
        }
        if (request.getNickname() != null) {
            String nickname = util.validateString(request.getNickname(), 50L, "닉네임");
            findUserInfo.setNickname(nickname);
        }
        if (request.getPhone() != null) {
            verifyPhoneNumberFormat(request.getPhone());
            String phone = request.getPhone().replaceAll(re.getPhone(), "0$1$2$3");
            findUserInfo.setPhone(phone);
        }
        return findUserInfo;
    }

    public UserInfo createAndSaveUserInfo(User user, SnsJoinReq request, String profileImage, Grade grade) {
        String fixedPhoneNumber = null;
        if (!request.getLoginType().equals(LoginType.APPLE)) {
            String phoneNumber = request.getPhone();
            verifyPhoneNumberFormat(phoneNumber);
            fixedPhoneNumber = phoneNumber.replaceAll(re.getPhone(), "0$1$2$3");
            verifyPhoneNumberIsExists(fixedPhoneNumber);
        }
        UserInfo userInfo = request.toUserInfo(fixedPhoneNumber);
        userInfo.setEmail(request.getEmail()); // 이메일 설정추가
        userInfo.setProfileImage(profileImage);
        userInfo.setGrade(grade);
        userInfo.setUserId(user.getId());
        userInfo.setUser(user);
        userInfo.setIsAgreeMarketing(false);

        return userInfoRepository.save(userInfo);
    }

    public UserInfo createAndSaveIdPwUserInfo(User user, UserJoinReq request, Grade grade) {

        String phoneNumber = request.getPhone();
        verifyPhoneNumberFormat(phoneNumber);

        String fixedPhoneNumber = phoneNumber.replaceAll(re.getPhone(), "0$1$2$3");
        verifyPhoneNumberIsExists(fixedPhoneNumber);

        String name = util.validateString(request.getName(), 20L, "이름");

        Boolean isAgreeMarketing = request.getIsAgreeMarketing() == null ? false : request.getIsAgreeMarketing();

        String nickname = util.validateString(request.getNickname(), 50L, "닉네임");
        if (userInfoRepository.findByNickname(nickname).isPresent()) {
            throw new IllegalArgumentException("중복된 닉네임입니다.");
        }

        UserInfo
                userInfo =
                UserInfo.builder()
                        .userId(user.getId())
                        .phone(fixedPhoneNumber)
                        .nickname(nickname)
                        .profileImage("")
                        .email(request.getEmail())
                        .name(name)
                        .point(0)
                        .isAgreeMarketing(isAgreeMarketing)
                        .grade(grade)
                        .build();
        userInfo.setUser(user);
        return userInfoRepository.save(userInfo);
    }

    public void setImageUrl(int userId, String imageUrl) {
        UserInfo
                findUserInfo =
                userInfoRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException(
                        "유저 정보를 찾을 수 없습니다"));
        findUserInfo.setProfileImage(imageUrl);
    }

    private void verifyPhoneNumberIsExists(String fixedPhoneNumber) {
        if (userInfoRepository.findByPhone(fixedPhoneNumber).isPresent()) {
            throw new IllegalArgumentException("회원가입된 이력이 있습니다.");
        }
    }

    private void verifyPhoneNumberFormat(String phoneNumber) {
        if (!Pattern.matches(re.phone, phoneNumber)) {
            throw new IllegalArgumentException("휴대폰 번호 형식을 확인해주세요.");
            // customResponse.throwError("휴대폰 번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
        }
    }
}
