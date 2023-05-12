package com.matsinger.barofishserver.store;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "store_info", schema = "barofish_dev", catalog = "")
public class StoreInfo {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreInfo that = (StoreInfo) o;
        return id == that.id &&
                Objects.equals(backgroudImage, that.backgroudImage) &&
                Objects.equals(profileImage, that.profileImage) &&
                Objects.equals(name, that.name) &&
                Objects.equals(location, that.location) &&
                Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, backgroudImage, profileImage, name, location, keyword);
    }
}
