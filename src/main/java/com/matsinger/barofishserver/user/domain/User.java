package com.matsinger.barofishserver.user.domain;

import com.matsinger.barofishserver.user.dto.UserDto;
import com.matsinger.barofishserver.userauth.domain.UserAuth;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user", schema = "barofish_dev", catalog = "")
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserInfo userInfo;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<UserAuth> userAuth = new ArrayList<>();
//    @Builder.Default
//    @OneToMany(mappedBy = "user")
//    @Column(name = "user_id", nullable = false)
//    private List<UserAuth> userAuths = new ArrayList<>();

    @Basic
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserState state;
    @Basic
    @Column(name = "join_at", nullable = false)
    private Timestamp joinAt;
    @Basic
    @Column(name = "withdraw_at", nullable = true)
    private Timestamp withdrawAt;

//    @OneToMany(mappedBy = "user")
//    private List<Review> reviews = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public Timestamp getJoinAt() {
        return joinAt;
    }

    public void setJoinAt(Timestamp joinAt) {
        this.joinAt = joinAt;
    }

    public void setWithdrawAt(Timestamp withdrawAt) {
        this.withdrawAt = withdrawAt;
    }

    public UserDto convert2Dto() {
        return UserDto.builder().id(this.id).state(this.state).joinAt(this.joinAt).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return id == that.id && Objects.equals(state, that.state) && Objects.equals(joinAt, that.joinAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, joinAt);
    }

}
