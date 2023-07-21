package com.matsinger.barofishserver.userinfo;

import com.matsinger.barofishserver.grade.domain.Grade;
import com.matsinger.barofishserver.grade.repository.GradeRepository;
import com.matsinger.barofishserver.user.repository.UserRepository;
import com.matsinger.barofishserver.user.application.UserCommandService;
import com.matsinger.barofishserver.user.dto.SnsJoinReq;
import com.matsinger.barofishserver.user.dto.UserUpdateReq;
import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.userinfo.application.UserInfoCommandService;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.userinfo.repository.UserInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.net.MalformedURLException;
import java.util.Optional;

import static com.matsinger.barofishserver.userauth.domain.LoginType.IDPW;
import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest
class UserInfoCommandServiceTest {

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCommandService userService;
    @Autowired
    private UserInfoCommandService userInfoCommandService;

    @AfterEach
    void tearDown() {
        userInfoRepository.deleteAllInBatch();
        gradeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("휴대폰 번호가 000-0000-000 형식에 맞지 않으면 예외를 던진다.")
    @Test
    void verifyPhoneInfoWithPhoneNumberFormat() throws MalformedURLException {
        // given
        Grade grade = createAndSaveGrade();
        String phoneNumber = "010-1234-123";
        SnsJoinReq request = createJoinRequest(phoneNumber);
        User createdUser = userService.createSnsUserAndSave(request);

        // when // then
        assertThatThrownBy(() -> userInfoCommandService.createAndSaveUserInfo(createdUser, request, "", grade))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("휴대폰 번호 형식을 확인해주세요.");
    }

    @DisplayName("휴대폰 번호가 000-0000-000 형식이면 UserInfo가 성공적으로 저장된다.")
    @Test
    void verifyPhoneInfoWithPhoneNumberFormat2() throws MalformedURLException {
        // given
        Grade grade = createAndSaveGrade();
        String phoneNumberFormat = "010-1234-1234";
        String fixedPhoneNumber = "01012341234";
        SnsJoinReq request = createJoinRequest(phoneNumberFormat);
        User createdUser = userService.createSnsUserAndSave(request);

        // when
        userInfoCommandService.createAndSaveUserInfo(createdUser, request, "", grade);

        // then
        assertThat(userInfoRepository.findByPhone(fixedPhoneNumber)).isPresent();
    }

    @DisplayName("기존 회원 정보에서 휴대폰 정보를 업데이트할 수 있다.")
    @Test
    void updateUserInfo1() throws Exception {
        // given
        String oldPhoneNumber = "010-1234-1234";

        String imageUrl = "";
        Grade createdGrade = createAndSaveGrade();
        User createdUser = userService.createIdPwUserAndSave();
        SnsJoinReq joinRequest = createJoinRequest(oldPhoneNumber);

        userInfoCommandService.createAndSaveUserInfo(createdUser, joinRequest, "", createdGrade);
        // when
        String newPhoneNumber = "010-1111-1111";
        UserUpdateReq request = UserUpdateReq.builder()
                .name("test")
                .nickname("test")
                .oldPassword("password1!")
                .newPassword("password2@")
                .phone(newPhoneNumber)
                .verificationId(1)
                .address("test")
                .addressDetail("test")
                .isAgreeMarketing(false)
                .build();
        UserInfo updatedUserInfo = userInfoCommandService.updateUserInfo(createdUser.getId(), request, imageUrl);
        UserInfo createdUserInfo = userInfoRepository.save(updatedUserInfo);

        // then
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByUserId(createdUser.getId());
        assertThat(optionalUserInfo).isPresent();
        assertThat(optionalUserInfo.get())
                .extracting("phone")
                .isEqualTo("01011111111");
    }

    @DisplayName("회원 정보를 업데이트할 때 휴대폰 정보가 형식에 맞지 않으면 오류가 발생한다.")
    @Test
    void updateUserInfoWithWrongPhoneNumberFormat() throws Exception {
        // given
        String oldPhoneNumber = "010-1234-1234";

        String imageUrl = "";
        Grade createdGrade = createAndSaveGrade();
        User createdUser = userService.createIdPwUserAndSave();
        SnsJoinReq joinRequest = createJoinRequest(oldPhoneNumber);

        userInfoCommandService.createAndSaveUserInfo(createdUser, joinRequest, "", createdGrade);
        // when
        String newPhoneNumber = "010-111-1111";
        UserUpdateReq request = UserUpdateReq.builder()
                .name("test")
                .nickname("test")
                .oldPassword("password1!")
                .newPassword("password2@")
                .phone(newPhoneNumber)
                .verificationId(1)
                .address("test")
                .addressDetail("test")
                .isAgreeMarketing(false)
                .build();

        // then
        assertThatThrownBy(() -> userInfoCommandService.updateUserInfo(createdUser.getId(), request, imageUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("휴대폰 번호 형식을 확인해주세요.");
    }

    private Grade createAndSaveGrade() {
        Grade grade = Grade.builder()
                .name("test")
                .pointRate(1)
                .minOrderCount(1)
                .minOrderPrice(1000)
                .build();
        return gradeRepository.save(grade);
    }

    private SnsJoinReq createJoinRequest(String phoneNumber) {
        return SnsJoinReq.builder()
                .loginType(IDPW)
                .loginId("test")
                .profileImage(null)
                .email("test@gmail.com")
                .name("test")
                .nickname("test")
                .phone(phoneNumber).build();
    }
}