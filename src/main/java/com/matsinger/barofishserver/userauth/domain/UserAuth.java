package com.matsinger.barofishserver.userauth.domain;

import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.userauth.dto.UserAuthDto;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserAuthId.class)
@Entity
@Table(name = "user_auth", schema = "barofish_dev")
public class UserAuth {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false, length = 20)
    private LoginType loginType;

    @Id
    @Column(name = "login_id", nullable = false, length = 150)
    private String loginId;

    @Basic
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // 연관관계 편의 메서드
    public void setUser(User user) {
        this.user = user;
        user.getUserAuth().add(this);
    }

    @Column(name = "password", length = 60)
    private String password;

    public UserAuthDto convert2Dto() {
        return UserAuthDto.builder().loginId(this.loginId).userId(this.userId).loginType(this.loginType).build();
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
