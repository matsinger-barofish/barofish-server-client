package com.matsinger.barofishserver.admin.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Column(name = "authority", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminAuthority authority;
    @Basic
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminState state;
    @Basic
    @Column(name = "name", nullable = false)
    private String name;
    @Basic
    @Column(name = "tel", nullable = false)
    private String tel;
    @Basic
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @OneToOne(mappedBy = "admin", fetch = FetchType.LAZY)
    private AdminAuth adminAuth;


    public int getId() {
        return id;
    }


    public String getLoginId() {
        return loginId;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setState(AdminState state) {
        this.state = state;
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
