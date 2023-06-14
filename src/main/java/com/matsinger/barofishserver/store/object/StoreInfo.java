package com.matsinger.barofishserver.store.object;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "store_info", schema = "barofish_dev", catalog = "")
public class StoreInfo {
    @Id
    @Column(name = "store_id", nullable = false)
    private int storeId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "store_id",updatable = false, insertable = false)
    private Store store;

    public void setStore(Store store) {
        this.store = store;
        store.setStoreInfo(this);
    }

    @Basic
    @Column(name = "backgroud_image", nullable = false, length = -1)
    private String backgroudImage;
    @Basic
    @Column(name = "profile_image", nullable = false, length = -1)
    private String profileImage;
    @Basic
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Basic
    @Column(name = "location", nullable = false, length = 50)
    private String location;
    @Basic
    @Column(name = "keyword", nullable = false, length = -1)
    private String keyword;

    @Basic
    @Column(name = "visit_note", nullable = false, columnDefinition = "TEXT")
    private String visitNote;

    public String getBackgroudImage() {
        return backgroudImage;
    }

    public void setBackgroudImage(String backgroudImage) {
        this.backgroudImage = backgroudImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public SimpleStore convert2Dto() {
        return SimpleStore.builder().storeId(this.store.getId()).isLike(false).backgroundImage(backgroudImage).profileImage(
                profileImage).name(name).location(location).keyword(keyword.split(",")).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreInfo that = (StoreInfo) o;
        return
//                storeId == that.storeId &&
                Objects.equals(backgroudImage, that.backgroudImage) &&
                        Objects.equals(profileImage, that.profileImage) &&
                        Objects.equals(name, that.name) &&
                        Objects.equals(location, that.location) &&
                        Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backgroudImage, profileImage, name, location, keyword);
    }
}

