package com.matsinger.barofishserver.review.object;

import com.matsinger.barofishserver.order.object.OrderProductInfo;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.store.object.Store;
import com.matsinger.barofishserver.user.object.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "review", schema = "barofish_dev", catalog = "")
public class Review {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
//    @Basic
//    @Column(name = "product_id", nullable = false)
//    private int productId;

    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    @Basic
    @Column(name = "product_id", nullable = false)
    private Integer productId;
    @ManyToOne
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    private Store store;
    @Basic
    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_product_info_id", insertable = false, updatable = false)
    private OrderProductInfo orderProductInfo;

    @Basic
    @Column(name = "order_product_info_id")
    private Integer orderProductInfoId;
    @Basic
    @Column(name = "images", nullable = false, length = -1)
    private String images;
    @Basic
    @Column(name = "content", nullable = false, length = -1)
    private String content;
    @Basic
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "review")
    private List<ReviewEvaluation> evaluations;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public Product getProduct() {
//        return product;
//    }
//
//    public void setProductId(Product product) {
//        this.product = product;
//    }

//    public int getStoreId() {
//        return storeId;
//    }
//
//    public void setStoreId(int storeId) {
//        this.storeId = storeId;
//    }

//    public int getUserId() {
//        return userId;
//    }
//
//    public void setUserId(int userId) {
//        this.userId = userId;
//    }


    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public ReviewDto convert2Dto() {
        return ReviewDto.builder().id(this.id).store(this.store.getStoreInfo().convert2Dto()).images(images.substring(1,
                images.length() -
                        1).split(",")).content(this.content).createdAt(this.createdAt).evaluations(this.evaluations !=
                null ? this.evaluations.stream().map(ReviewEvaluation::getEvaluation).toList() : null).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review that = (Review) o;
        return id == that.id &&
//                productId == that.productId &&
//                storeId == that.storeId &&
//                userId == that.userId &&
                Objects.equals(images, that.images) &&
                Objects.equals(content, that.content) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, images, content, createdAt);
    }
}
