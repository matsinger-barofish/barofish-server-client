package com.matsinger.barofishserver.domain.inquiry.domain;

import com.matsinger.barofishserver.domain.inquiry.dto.InquiryDto;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
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
    @ManyToOne
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;
    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;
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

    public void setType(InquiryType type) {
        this.type = type;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAnsweredAt(Timestamp answeredAt) {
        this.answeredAt = answeredAt;
    }

    public void setIsSecret(Boolean isSecret) {
        this.isSecret = isSecret;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public InquiryDto convert2Dto() {
        return InquiryDto.builder().id(this.getId()).type(this.getType()).isSecret(this.getIsSecret()).productId(this.getProductId()).content(
                this.getContent()).createdAt(this.getCreatedAt()).answeredAt(this.getAnsweredAt()).answer(this.getAnswer()).user(
                user.getUserInfo() != null ? user.getUserInfo().convert2Dto() : null).build();
    }

}
