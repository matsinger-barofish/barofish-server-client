package com.matsinger.barofishserver.product.object;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.matsinger.barofishserver.category.Category;
import com.matsinger.barofishserver.review.object.Review;
import com.matsinger.barofishserver.store.object.Store;
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

    @Basic
    @Column(name = "store_id", nullable = false)
    private int storeId;

    @ManyToOne
    @JoinColumn(name = "store_id", updatable = false, insertable = false)
    private Store store;

    // @Basic
    // @Column(name = "category_id", nullable = false)
    // private int categoryId;

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
    @Column(name = "expected_deliver_day", nullable = false)
    private int expectedDeliverDay;
    @Basic
    @Column(name = "point_rate", nullable = true)
    private Float pointRate;
    @Basic
    @Column(name = "represent_item_id", nullable = true)
    private Integer representOptionItemId;

    @Basic
    @Column(name = "deliver_box_per_amount", nullable = true)
    private Integer deliverBoxPerAmount;

    @Basic
    @Column(name = "need_taxation", nullable = false)
    private Boolean needTaxation;

    @Basic
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Builder.Default
    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "product")
    private List<Review> reviews = new ArrayList<>();
    @Column(name = "delivery_fee", nullable = true)
    private int deliveryFee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ProductListDto convert2ListDto() {
        return ProductListDto.builder().id(this.id).image(this.images.substring(1,
                images.length() - 1).split(",")[0]).originPrice(this.originPrice).title(this.title).build();
    }

    public SimpleProductDto convert2SimpleDto() {

        return SimpleProductDto.builder().id(this.getId()).category(this.category.convert2Dto()).expectedDeliverDay(this.getExpectedDeliverDay()).images(
                this.images.substring(1,
                        images.length() -
                                1).split(",")).title(title).state(this.getState()).originPrice(originPrice).deliveryInfo(
                deliveryInfo).deliveryFee(deliveryFee).description(descriptionImages).descriptionImages(
                descriptionImages.substring(1,
                        descriptionImages.length() -
                                1).split(",")).representOptionItemId(this.representOptionItemId).deliverBoxPerAmount(
                this.deliverBoxPerAmount).createdAt(this.getCreatedAt()).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product that = (Product) o;
        return id == that.id &&
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
        return Objects.hash(id,
                state,
                images,
                title,
                originPrice,
                discountRate,
                deliveryInfo,
                descriptionImages,
                createdAt);
    }

}
