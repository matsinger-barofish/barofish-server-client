package com.matsinger.barofishserver.review.domain;

import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.review.dto.ReviewDto;
import com.matsinger.barofishserver.store.domain.Store;
import com.matsinger.barofishserver.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "review", schema = "barofish_dev", catalog = "")
public class Review {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

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

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @OneToMany(mappedBy = "review")
    private List<ReviewEvaluation> evaluations;

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setImages(String images) {
        this.images = images;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public ReviewDto convert2Dto() {
        return ReviewDto.builder().id(this.id).store(this.store.getStoreInfo().convert2Dto()).images(images.substring(1,
                images.length() -
                        1).split(",")).content(this.content).createdAt(this.createdAt).evaluations(this.evaluations !=
                null ? this.evaluations.stream().map(ReviewEvaluation::getEvaluation).toList() : null).build();
    }
}
