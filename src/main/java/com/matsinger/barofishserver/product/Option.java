package com.matsinger.barofishserver.product;

import com.matsinger.barofishserver.store.Store;
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
@Table(name = "options", schema = "barofish_dev", catalog = "")
public class Option {
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

    // 연관관계 편의 메서드
    public void setProduct(Product product) {
        this.product = product;
        product.getOptions().add(this);
    }

    public void reduceAmount(int amount) {
        optionItem.reduceAmount(amount);
    }

    @Basic
    @Column(name = "is_needed", nullable = false)
    private byte isNeeded;
    @Basic
    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @OneToOne(mappedBy = "option")
    private OptionItem optionItem;

    public double getDiscountRate() {
        return optionItem.getDiscountRate();
    }

    public void setOptionItem(OptionItem optionItem) {
        this.optionItem = optionItem;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProductId() {
        return product;
    }

    public void setProductId(Product product) {
        this.product = product;
    }

    public byte getIsNeeded() {
        return isNeeded;
    }

    public void setIsNeeded(byte isNeeded) {
        this.isNeeded = isNeeded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStoreId() {
        return product.getStoreId();
    }

    public Store getStore() {
        return product.getStore();
    }

    public int getPrice() {
        return optionItem.getPrice();
    }

    public String getName() {
        return optionItem.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Option that = (Option) o;
        return id == that.id &&
//                productId == that.productId &&
                isNeeded == that.isNeeded && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, isNeeded, description);
    }
}
