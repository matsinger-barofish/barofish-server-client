package com.matsinger.barofishserver.user;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "deliver_place", schema = "barofish_dev", catalog = "")
public class DeliverPlace {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;
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
    @Column(name = "address", nullable = false, length = 100)
    private String address;
    @Basic
    @Column(name = "address_detail", nullable = false, length = 100)
    private String addressDetail;
    @Basic
    @Column(name = "deliver_message", nullable = false, length = 100)
    private String deliverMessage;
    @Basic
    @Column(name = "is_default", nullable = false)
    private byte isDefault;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public byte getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(byte isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, receiverName, tel, address, addressDetail, deliverMessage, isDefault);
    }
}
