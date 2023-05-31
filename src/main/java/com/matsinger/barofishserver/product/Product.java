package com.matsinger.barofishserver.product;

import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.product.productinfo.*;
import com.matsinger.barofishserver.review.Review;
import com.matsinger.barofishserver.store.Store;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

<<<<<<< HEAD
    @Basic
    @Column(name = "type_id", nullable = false)
    private Integer typeId;

    @OneToMany
    @JoinColumn(name = "product_id")
    private List<Option> options = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Review>
            imageReviews =
            reviews.stream().filter(review -> review.getImages().length() != 0).collect(Collectors.toList());
=======
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
    private List<Option> options = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
    private List<Review> reviews = new ArrayList<>();

    // 주석 풀면 에러 발생
    // Caused by: java.lang.reflect.InvocationTargetException: null
    //Caused by: java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because "this.reviews" is null
//    @OneToMany
//    private List<Review>
//            imageReviews =
//            reviews.stream().filter(review -> review.getImages().length() != 0).collect(Collectors.toList());
>>>>>>> origin/order

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id")
    private ProductType productType;

    @Column(name = "deliveryFee", nullable = false)
    private int deliveryFee;

    public void setProductType(ProductType productType) {
        this.productType = productType;
        productType.setProduct(this);
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private ProductLocation productLocation;

    public void setProductLocation(ProductLocation productLocation) {
        this.productLocation = productLocation;
        productLocation.setProduct(this);
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private ProductProcess productProcess;

    public void setProductProcess(ProductProcess productProcess) {
        this.productProcess = productProcess;
        productProcess.setProduct(this);
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "usage_id")
    private ProductUsage productUsage;

    public void setProductUsage(ProductUsage productUsage) {
        this.productUsage = productUsage;
        productUsage.setProduct(this);
    }

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_id")
    private ProductStorage productStorage;

    public void setProductStorage(ProductStorage productStorage) {
        this.productStorage = productStorage;
        productStorage.setProduct(this);
    }

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
