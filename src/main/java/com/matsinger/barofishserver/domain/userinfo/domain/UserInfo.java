package com.matsinger.barofishserver.domain.userinfo.domain;

import com.matsinger.barofishserver.domain.grade.domain.Grade;
import com.matsinger.barofishserver.domain.user.domain.User;
import com.matsinger.barofishserver.domain.userinfo.dto.UserInfoDto;
import com.matsinger.barofishserver.global.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_info", schema = "barofish_dev", catalog = "")
public class UserInfo {
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Basic
    @Column(name = "profile_image", nullable = false, length = -1)
    private String profileImage;
    @Basic
    @Column(name = "email", nullable = false, length = 300)
    private String email;
    @Basic
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Basic
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;
    @Basic
    @Column(name = "phone", nullable = true, length = 11)
    private String phone;
    @Basic
    @Column(name = "is_agree_marketing", nullable = false)
    private Boolean isAgreeMarketing;

    @Basic
    @Column(name = "point", nullable = false)
    private Integer point;

    @ManyToOne
    @JoinColumn(name = "grade_id", insertable = true, updatable = false)
    private Grade grade;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getIsAgreeMarketing() {
        return isAgreeMarketing;
    }

    public void setIsAgreeMarketing(Boolean isAgreeMarketing) {
        this.isAgreeMarketing = isAgreeMarketing;
    }

    public UserInfoDto convert2Dto() {
        return UserInfoDto.builder().userId(this.getUserId()).profileImage(this.getProfileImage())
                .email(this.getEmail()).name(
                        this.getName())
                .nickname(this.getNickname()).phone(this.getPhone()).isAgreeMarketing(this.getIsAgreeMarketing()).point(
                        this.getPoint())
                .grade(this.getGrade()).build();
    }

    public void validatePoint(Integer point) {
        if (point != null && this.point < point) {
            throw new BusinessException("보유한 적립금보다 많은 적립금입니다.");
        }
    }
    public void usePoint(Integer pointToUse) {
        if (pointToUse != null && point < pointToUse) {
            throw new BusinessException("보유한 적립금보다 많은 적립금입니다.");
        }
        this.point -= pointToUse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserInfo userInfo = (UserInfo) o;
        return userId == userInfo.userId &&
                isAgreeMarketing == userInfo.isAgreeMarketing &&
                Objects.equals(profileImage, userInfo.profileImage) &&
                Objects.equals(email, userInfo.email) &&
                Objects.equals(nickname, userInfo.nickname) &&
                Objects.equals(phone, userInfo.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, profileImage, email, nickname, phone, isAgreeMarketing);
    }
}
