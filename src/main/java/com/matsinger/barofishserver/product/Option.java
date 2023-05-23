package com.matsinger.barofishserver.product;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "option", schema = "barofish_dev", catalog = "")
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

    @Basic
    @Column(name = "is_needed", nullable = false)
    private byte isNeeded;
    @Basic
    @Column(name = "description", nullable = false, length = 200)
    private String description;

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
