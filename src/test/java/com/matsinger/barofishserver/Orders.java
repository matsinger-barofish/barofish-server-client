package com.matsinger.barofishserver;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Orders {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, length = 20)
    private String id;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic
    @Column(name = "state", nullable = false)
    private Object state;
    @Basic
    @Column(name = "total_price", nullable = false)
    private int totalPrice;
    @Basic
    @Column(name = "ordered_at", nullable = false)
    private Timestamp orderedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Timestamp getOrderedAt() {
        return orderedAt;
    }

    public void setOrderedAt(Timestamp orderedAt) {
        this.orderedAt = orderedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Orders orders = (Orders) o;
        return userId == orders.userId &&
                totalPrice == orders.totalPrice &&
                Objects.equals(id, orders.id) &&
                Objects.equals(state, orders.state) &&
                Objects.equals(orderedAt, orders.orderedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, state, totalPrice, orderedAt);
    }
}
