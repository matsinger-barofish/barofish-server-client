package com.matsinger.barofishserver.store.object;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.matsinger.barofishserver.review.Review;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "store", schema = "barofish_dev", catalog = "")
@Getter
@Setter
public class Store {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private StoreState state;
    @Basic
    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;
    @Basic
    @Column(name = "password", nullable = false, length = 60)
    private String password;
    @Basic
    @Column(name = "join_at", nullable = false)
    private Timestamp joinAt;


    @OneToOne(mappedBy = "store")
    private StoreInfo storeInfo;

    @OneToMany(mappedBy = "store")
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();

    public void setStoreInfo(StoreInfo storeInfo) {
        this.storeInfo = storeInfo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StoreState getState() {
        return state;
    }

    public void setState(StoreState state) {
        this.state = state;
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

    public Timestamp getJoinAt() {
        return joinAt;
    }

    public void setJoinAt(Timestamp joinAt) {
        this.joinAt = joinAt;
    }

    public String getName() {
        return storeInfo.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store that = (Store) o;
        return id == that.id &&
                Objects.equals(state, that.state) &&
                Objects.equals(loginId, that.loginId) &&
                Objects.equals(password, that.password) &&
                Objects.equals(joinAt, that.joinAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, loginId, password, joinAt);
    }

    public StoreDto convert2Dto() {
        return StoreDto.builder().id(this.getId()).state(this.getState()).loginId(this.getLoginId()).joinAt(this.joinAt).backgroundImage(
                this.getStoreInfo().getBackgroudImage()).profileImage(this.getStoreInfo().getProfileImage()).name(this.getStoreInfo().getName()).location(
                this.getStoreInfo().getLocation()).keyword(this.getStoreInfo().getKeyword().split(",")).build();
    }
}
