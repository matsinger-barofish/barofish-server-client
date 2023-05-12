package com.matsinger.barofishserver.inquiry;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "inquiry", schema = "barofish_dev", catalog = "")
public class Inquiry {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "type", nullable = false)
    private Object type;
    @Basic
    @Column(name = "is_secret", nullable = false)
    private byte isSecret;
    @Basic
    @Column(name = "product_id", nullable = false)
    private int productId;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    @Basic
    @Column(name = "content", nullable = false, length = -1)
    private String content;
    @Basic
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
    @Basic
    @Column(name = "answeted_at", nullable = true)
    private Timestamp answetedAt;

    public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Object getType() {
            return type;
        }

        public void setType(Object type) {
            this.type = type;
        }

        public byte getIsSecret() {
            return isSecret;
        }

        public void setIsSecret(byte isSecret) {
            this.isSecret = isSecret;
        }

        public int getProductId() {
            return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Timestamp getAnswetedAt() {
        return answetedAt;
    }

    public void setAnswetedAt(Timestamp answetedAt) {
        this.answetedAt = answetedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inquiry that = (Inquiry) o;
        return id == that.id &&
                isSecret == that.isSecret &&
                productId == that.productId &&
                userId == that.userId &&
                Objects.equals(type, that.type) &&
                Objects.equals(title, that.title) &&
                Objects.equals(content, that.content) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(answetedAt, that.answetedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, isSecret, productId, userId, title, content, createdAt, answetedAt);
    }
}
