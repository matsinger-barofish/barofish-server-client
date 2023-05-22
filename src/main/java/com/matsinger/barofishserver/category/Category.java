package com.matsinger.barofishserver.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "category", schema = "barofish_dev", catalog = "")
public class Category {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "category_id", nullable = true)
    private Integer categoryId;
    @Basic
    @Column(name = "image", nullable = true, length = -1)
    private String image;
    @Basic
    @Column(name = "name", nullable = false, length = 20)
    private String name;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",  referencedColumnName = "id", insertable = false, updatable = false)
    private Category parentCategory;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentCategory")
    private List<Category> categoryList = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        if (this.parentCategory != null) return this.parentCategory.id;
        else return null;
    }

    public void setCategoryId(Integer categoryId) {
        this.parentCategory.id = categoryId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category that = (Category) o;
        return id == that.id &&
//                Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(image, that.image) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, image, name);
    }
}
