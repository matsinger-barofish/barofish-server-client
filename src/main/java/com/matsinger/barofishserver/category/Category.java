package com.matsinger.barofishserver.category;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Column(name = "parent_category_id", nullable = true)
    private Integer categoryId;
    @Basic
    @Column(name = "image", nullable = true, length = -1)
    private String image;
    @Basic
    @Column(name = "name", nullable = false, length = 20)
    private String name;
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id", referencedColumnName = "id", insertable = false, updatable = false)
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
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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
                // Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(image, that.image) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, image, name);
    }

    public CategoryDto convert2Dto() {
        List<CategoryDto> categories = new ArrayList<>();
        String parentName = null;
        if (this.categoryId == null) {
            for (Category category : this.categoryList) {
                categories.add(category.convert2Dto());
            }
        } else {
            parentName = this.parentCategory.getName();
        }
        return CategoryDto.builder().id(this.id).parentId(this.categoryId).name(this.name).image(this.image).ParentCategoryName(
                parentName).categories(categories).build();
    }
}
