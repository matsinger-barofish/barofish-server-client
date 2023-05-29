package com.matsinger.barofishserver.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "option_item", schema = "barofish_dev", catalog = "")
public class OptionItem {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "option_id")
    private Option option;

    // 연관관계 편의 메서드
    public void setOption(Option option) {
        this.option = option;
        option.setOptionItem(this);
    }

    @Basic
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Basic
    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "discount_rate", nullable = false)
    private double discountRate;

    @Basic
    @Column(name = "amount", nullable = false)
    private int amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOptionId() {
        return option.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStoreId() {
        return option.getStoreId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionItem that = (OptionItem) o;
        return id == that.id && option.getId() == that.getOptionId() && price == that.price && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getOptionId(), name, price);
    }
}
