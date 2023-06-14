package com.matsinger.barofishserver.order.object;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_deliver_place", schema = "barofish_dev", catalog = "")
public class OrderDeliverPlace {
    @Id
    @Column(name = "order_id", nullable = false, length = 20)
    private String orderId;
    @Basic
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Basic
    @Column(name = "receiver_name", nullable = false, length = 20)
    private String receiverName;
    @Basic
    @Column(name = "tel", nullable = false, length = 11)
    private String tel;
    @Basic
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;
    @Basic
    @Column(name = "address", nullable = false, length = 100)
    private String address;
    @Basic
    @Column(name = "address_detail", nullable = false, length = 100)
    private String addressDetail;
    @Basic
    @Column(name = "deliver_message", nullable = false, length = 100)
    private String deliverMessage;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getDeliverMessage() {
        return deliverMessage;
    }

    public void setDeliverMessage(String deliverMessage) {
        this.deliverMessage = deliverMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDeliverPlace that = (OrderDeliverPlace) o;
        return Objects.equals(orderId, that.orderId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(receiverName, that.receiverName) &&
                Objects.equals(tel, that.tel) &&
                Objects.equals(address, that.address) &&
                Objects.equals(addressDetail, that.addressDetail) &&
                Objects.equals(deliverMessage, that.deliverMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, name, receiverName, tel, address, addressDetail, deliverMessage);
    }
}
