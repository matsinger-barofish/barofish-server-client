package com.matsinger.barofishserver.product;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.review.Review;
import com.matsinger.barofishserver.store.Store;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "product", schema = "barofish_dev", catalog = "")
public class Product {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

//    @Basic
//    @Column(name = "store_id", nullable = false)
//    private int storeId;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

//    @Basic
//    @Column(name = "category_id", nullable = false)
//    private int categoryId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    private ProductState state;
    @Basic
    @Column(name = "images", nullable = false, length = -1)
    private String images;
    @Basic
    @Column(name = "title", nullable = false, length = 100)
    private String title;
    @Basic
    @Column(name = "origin_price", nullable = false)
    private int originPrice;
    @Basic
    @Column(name = "discount_rate", nullable = false)
    private int discountRate;
    @Basic
    @Column(name = "delivery_info", nullable = false, length = -1)
    private String deliveryInfo;
    @Basic
    @Column(name = "description_images", nullable = false, length = -1)
    private String descriptionImages;
    @Basic
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @OneToMany
    @JoinColumn(name = "product_id")
    private List<Option> options = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "product_id")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany
    private List<Review>
            imageReviews =
            reviews.stream().filter(review -> review.getImages().length() != 0).collect(Collectors.toList());


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Integer getStoreId() {
        return store.getId();
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getCategoryId() {
        return category.getId();
    }

    public ProductState getState() {
        return state;
    }

    public void setState(ProductState state) {
        this.state = state;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(int originPrice) {
        this.originPrice = originPrice;
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
    }

    public String getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(String deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public String getDescriptionImages() {
        return descriptionImages;
    }

    public void setDescriptionImages(String descriptionImages) {
        this.descriptionImages = descriptionImages;
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
        Product that = (Product) o;
        return id == that.id &&
//                storeId == that.storeId &&
//                categoryId == that.categoryId &&
                originPrice == that.originPrice &&
                discountRate == that.discountRate &&
                Objects.equals(state, that.state) &&
                Objects.equals(images, that.images) &&
                Objects.equals(title, that.title) &&
                Objects.equals(deliveryInfo, that.deliveryInfo) &&
                Objects.equals(descriptionImages, that.descriptionImages) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, images, title, originPrice, discountRate, deliveryInfo, descriptionImages, createdAt);
    }
}
