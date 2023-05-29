package com.matsinger.barofishserver.order;

import com.matsinger.barofishserver.order.dto.response.OrderProductInfoDto;
import com.matsinger.barofishserver.order.dto.response.OrderStoreInfoDto;
import com.matsinger.barofishserver.store.Store;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_store_info", schema = "barofish_dev", catalog = "")
public class OrderStoreInfo {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        order.getOrderStoreInfos().add(this);
    }

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderStoreInfo")
    @Column(name = "order_product_info", nullable = false)
    @Builder.Default
    private List<OrderProductInfo> orderProductInfos = new ArrayList<>();

    @Column(name = "price", nullable = false)
    private int price;

    public void setPrice(int price) {
        this.price = price;
    }

    public OrderStoreInfoDto toDto(List<OrderProductInfoDto> productInfoDtos) {
        return OrderStoreInfoDto.builder()
                .storeId(store.getId())
                .storeName(store.getName())
                .price(price)
                .storeProducts(productInfoDtos).build();
    }
}
