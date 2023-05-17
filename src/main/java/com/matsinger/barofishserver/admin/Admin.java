package com.matsinger.barofishserver.admin;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Admin {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;
    @Basic
    @Column(name = "password", nullable = false, length = 60)
    private String password;
    @Basic
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminState state;
    @Basic
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AdminState getState() {
        return state;
    }

    public void setState(AdminState state) {
        this.state = state;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return id == admin.id &&
                Objects.equals(loginId, admin.loginId) &&
                Objects.equals(password, admin.password) &&
                Objects.equals(state, admin.state) &&
                Objects.equals(createdAt, admin.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, loginId, password, state, createdAt);
    }
}
