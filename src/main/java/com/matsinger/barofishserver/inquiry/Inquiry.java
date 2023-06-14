package com.matsinger.barofishserver.inquiry;

import com.matsinger.barofishserver.product.object.Product;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "inquiry", schema = "barofish_dev", catalog = "")
public class Inquiry {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InquiryType type;
    @Basic
    @Column(name = "is_secret", nullable = false)
    private Boolean isSecret;
    @Basic
    @Column(name = "product_id", nullable = false)
    private int productId;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Basic
    @Column(name = "content", nullable = false, length = -1)
    private String content;
    @Basic
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
    @Basic
    @Column(name = "answer", nullable = true)
    private String answer;
    @Basic
    @Column(name = "answered_at", nullable = true)
    private Timestamp answeredAt;

    @ManyToOne
    @JoinColumn(name = "product_id",insertable = false,updatable = false)
    private Product product;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public InquiryType getType() {
        return type;
    }

    public void setType(InquiryType type) {
        this.type = type;
    }

    public Boolean getIsSecret() {
        return isSecret;
    }

    public void setIsSecret(Boolean isSecret) {
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
        return answeredAt;
    }

    public void setAnswetedAt(Timestamp answetedAt) {
        this.answeredAt = answetedAt;
    }

    public InquiryDto convert2Dto(){
        return InquiryDto.builder()
                .id(this.getId())
                .type(this.getType())
                .isSecret(this.getIsSecret())
                .productId(this.getProductId())
                .content(this.getContent())
                .createdAt(this.getCreatedAt())
                .answeredAt(this.getAnsweredAt())
                .answer(this.getAnswer())
                         .build();
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
                Objects.equals(content, that.content) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(answeredAt, that.answeredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, isSecret, productId, userId, content, createdAt, answeredAt);
    }
}
