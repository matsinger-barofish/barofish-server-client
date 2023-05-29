package com.matsinger.barofishserver.order;

import com.matsinger.barofishserver.payment.Payment;
import com.matsinger.barofishserver.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders", schema = "barofish_dev", catalog = "")
public class Order {
    @Id
    @Column(name = "id", nullable = false, length = 20)
    private String id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * DB 구조 변경. Store 기준으로 OrderProductInfo를 한번 더 감싸줌
     */
    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "order")
    @Column(name = "order_store_infos", nullable = false)
    private List<OrderStoreInfo> orderStoreInfos = new ArrayList<>();

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private OrderState state;

    @Basic
    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Basic
    @Column(name = "ordered_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime orderedAt;

    @OneToOne(mappedBy = "order")
//    @Column(name = "payment_id", nullable = true)
    private Payment payment;

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return user.getId();
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order that = (Order) o;
        return user.getId() == that.user.getId() &&
                Objects.equals(id, that.id) &&
                Objects.equals(state, that.state) &&
                Objects.equals(totalPrice, that.totalPrice) &&
                Objects.equals(orderedAt, that.orderedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user.getId(), state, totalPrice, orderedAt);
    }
}
