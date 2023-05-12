package com.matsinger.barofishserver.banner;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "banner", schema = "barofish_dev", catalog = "")
public class Banner {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "state", nullable = false)
    private Object state;
    @Basic
    @Column(name = "type", nullable = false)
    private Object type;
    @Basic
    @Column(name = "image", nullable = false, length = -1)
    private String image;
    @Basic
    @Column(name = "curation_id", nullable = true)
    private Integer curationId;
    @Basic
    @Column(name = "notice_id", nullable = true)
    private Integer noticeId;
    @Basic
    @Column(name = "category_id", nullable = true)
    private Integer categoryId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getQurationId() {
        return curationId;
    }

    public void setQurationId(Integer qurationId) {
        this.curationId = qurationId;
    }

    public Integer getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Integer noticeId) {
        this.noticeId = noticeId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Banner that = (Banner) o;
        return id == that.id &&
                Objects.equals(state, that.state) &&
                Objects.equals(type, that.type) &&
                Objects.equals(image, that.image) &&
                Objects.equals(curationId, that.curationId) &&
                Objects.equals(noticeId, that.noticeId) &&
                Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, type, image, curationId, noticeId, categoryId);
    }
}
