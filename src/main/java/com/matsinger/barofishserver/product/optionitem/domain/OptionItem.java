package com.matsinger.barofishserver.product.optionitem.domain;

import com.matsinger.barofishserver.product.domain.OptionItemState;
import com.matsinger.barofishserver.product.optionitem.dto.OptionItemDto;
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
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private OptionItemState state;

    @Basic
    @Column(name = "discount_price", nullable = false)
    private int discountPrice;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Basic
    @Column(name = "purchase_price", nullable = false)
    private Integer purchasePrice;

    @Basic
    @Column(name = "origin_price", nullable = false)
    private Integer originPrice;

    @Basic
    @Column(name = "delivery_fee", nullable = false)
    private Integer deliverFee;

    @Basic
    @Column(name = "deliver_box_per_amount", nullable = true)
    private Integer deliverBoxPerAmount;

    @Basic
    @Column(name = "max_available_amount", nullable = true)
    private Integer maxAvailableAmount;


    public void reduceAmount(int amount) throws Exception {
        if (this.amount != null) {
            int reducedValue = this.amount - amount;
            if (reducedValue < 0) {
                String errorMessage = String.format("'%s' 상품의 재고가 부족합니다.", this.name);
                throw new Exception(errorMessage);
            }
//            this.amount = reducedValue;
        }
    }

    public OptionItemDto convert2Dto() {
        return OptionItemDto.builder().id(this.id).optionId(this.getOptionId()).name(this.name).discountPrice(this.discountPrice).amount(
                this.amount).purchasePrice(this.purchasePrice).originPrice(this.originPrice).deliveryFee(this.deliverFee).deliverBoxPerAmount(
                this.deliverBoxPerAmount).maxAvailableAmount(this.maxAvailableAmount).build();
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptionItem that = (OptionItem) o;
        return id == that.id && optionId == that.getOptionId() && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getOptionId(), name);
    }

}
