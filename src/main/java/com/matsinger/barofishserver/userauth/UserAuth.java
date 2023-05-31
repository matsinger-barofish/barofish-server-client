package com.matsinger.barofishserver.userauth;

import com.matsinger.barofishserver.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    // 연관관계 편의 메서드
    public void setUser(User user) {
        this.user = user;
        user.getUserAuths().add(this);
    }

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    public int getUserId() {
        return user.getId();
    }
}
