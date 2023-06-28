package com.matsinger.barofishserver.user.object;

import com.matsinger.barofishserver.userauth.UserAuth;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
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

    @OneToMany(mappedBy = "user")
    private List<UserAuth> userAuth;
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
