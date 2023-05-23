package com.matsinger.barofishserver.review;

import com.matsinger.barofishserver.product.Product;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
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
    @JoinColumn(name = "product_id")
    private Product product;

    @Basic
    @Column(name = "store_id", nullable = false)
    private int storeId;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic
    @Column(name = "evaluation", nullable = false)
    private Object evaluation;
    @Basic
    @Column(name = "images", nullable = false, length = -1)
    private String images;
    @Basic
    @Column(name = "content", nullable = false, length = -1)
    private String content;
    @Basic
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProductId(Product product) {
        this.product = product;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Object getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Object evaluation) {
        this.evaluation = evaluation;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review that = (Review) o;
        return id == that.id &&
//                productId == that.productId &&
                storeId == that.storeId &&
                userId == that.userId &&
                Objects.equals(evaluation, that.evaluation) &&
                Objects.equals(images, that.images) &&
                Objects.equals(content, that.content) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, storeId, userId, evaluation, images, content, createdAt);
    }
}
