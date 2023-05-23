package com.matsinger.barofishserver.user;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "user_info", schema = "barofish_dev", catalog = "")
public class UserInfo {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    public int getId() {
        return userId;
    }

    public void setId(int id) {
        this.userId = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo that = (UserInfo) o;
        return userId == that.userId && Objects.equals(nickname, that.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, nickname);
    }
}
