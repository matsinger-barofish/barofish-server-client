package com.matsinger.barofishserver.domain.data.curation.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.matsinger.barofishserver.domain.data.curation.dto.CurationDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@Table(name = "curation", schema = "barofish_dev", catalog = "")
public class Curation {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "image", nullable = true, length = -1)
    private String image;
    @Basic
    @Column(name = "short_name", nullable = true, length = 20)
    private String shortName;
    @Basic
    @Column(name = "title", nullable = true, length = 100)
    private String title;
    @Basic
    @Column(name = "description", nullable = true, length = 200)
    private String description;
    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = true)
    private CurationType type;

    @Basic
    @Column(name = "sort_no", nullable = false)
    private Integer sortNo;


    @OneToMany(mappedBy = "curation", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<CurationProductMap> curationProductMaps = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 10)
    private CurationState state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public CurationType getType() {
        return type;
    }

    public void setType(CurationType type) {
        this.type = type;
    }

    public CurationDto convert2Dto() {
        return CurationDto.builder()
                .id(this.getId())
                .shortName(this.getShortName())
                .title(this.getTitle())
                .image(this.getImage())
                .sortNo(this.getSortNo())
                .type(this.getType())
                .description(this.getDescription())
                .state(this.state)
//                .state(this.state)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Curation that = (Curation) o;
        return id == that.id &&
                Objects.equals(image, that.image) &&
                Objects.equals(shortName, that.shortName) &&
                Objects.equals(title, that.title) &&
                Objects.equals(description, that.description) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, image, shortName, title, description, type);
    }
}
