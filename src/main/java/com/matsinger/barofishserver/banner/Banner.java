package com.matsinger.barofishserver.banner;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.data.curation.object.Curation;
import com.matsinger.barofishserver.notice.Notice;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "banner", schema = "barofish_dev", catalog = "")
public class Banner {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private BannerState state;
    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BannerType type;
    @Basic
    @Column(name = "image", nullable = false, length = -1)
    private String image;

    @Basic
    @Column(name = "sort_no",nullable = true)
    private Integer sortNo;
    @Basic
    @Column(name = "link", nullable = true)
    private String link;
    @Basic
    @Column(name = "curation_id", nullable = true)
    private Integer curationId;
    @ManyToOne
    @JoinColumn(name = "curation_id", updatable = false, insertable = false)
    private Curation curation;
    @Basic
    @Column(name = "notice_id", nullable = true)
    private Integer noticeId;
    @ManyToOne
    @JoinColumn(name = "notice_id", insertable = false, updatable = false)
    private Notice notice;
    @Basic
    @Column(name = "category_id", nullable = true)
    private Integer categoryId;

    @ManyToOne
    @JoinColumn(name = "category_id", updatable = false, insertable = false)
    private Category category;

    public BannerDto convert2Dto() {
        return BannerDto.builder().id(this.id).state(this.state).type(this.type).curationId(this.curation !=
                null ? this.curation.getId() : null).curationName(this.curation !=
                null ? this.curation.getTitle() : null).noticeId(this.notice !=
                null ? this.notice.getId() : null).noticeTitle(this.notice !=
                null ? this.notice.getTitle() : null).categoryId(this.category !=
                null ? this.category.getId() : null).categoryName(this.category !=
                null ? this.category.getName() : null).image(this.image).link(this.link).sortNo(this.sortNo).build();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BannerState getState() {
        return state;
    }

    public void setState(BannerState state) {
        this.state = state;
    }

    public BannerType getType() {
        return type;
    }

    public void setType(BannerType type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCurationId() {
        return curationId;
    }

    public void setCurationId(Integer curationId) {
        this.curationId = curationId;
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
