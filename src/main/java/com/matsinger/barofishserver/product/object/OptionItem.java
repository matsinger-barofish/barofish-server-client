package com.matsinger.barofishserver.product.object;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
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

    @Basic
    @Column(name = "option_id", nullable = false)
    private int optionId;
    @Basic
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Basic
    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "discount_rate", nullable = false)
    private Integer discountRate;

    @Basic
    @Column(name = "amount", nullable = true)
    private Integer amount;

    public void reduceAmount(int amount) {
        int reducedValue = this.amount - amount;
        if (reducedValue < 0) {
            String errorMessage = String.format("'%s' 상품의 재고가 부족합니다.", this.name);
            throw new Error(errorMessage);
        }
        this.amount = reducedValue;
    }

    public OptionItemDto convert2Dto() {
        return OptionItemDto.builder().optionId(this.getOptionId()).id(this.getId()).name(this.getName()).discountRate(
                this.getDiscountRate()).price(this.getPrice()).amount(this.getAmount()).build();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionItem that = (OptionItem) o;
        return id == that.id &&
                optionId == that.getOptionId() &&
                price == that.price &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getOptionId(), name, price);
    }
}
