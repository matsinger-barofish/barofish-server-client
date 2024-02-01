package com.matsinger.barofishserver.domain.payment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments", schema = "barofish_dev", catalog = "")
public class Payments {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "order_id", nullable = false, length = 20)
    private String orderId;
    @Basic
    @Column(name = "imp_uid", nullable = false, length = 30)
    private String impUid;
    @Basic
    @Column(name = "merchant_uid", nullable = false, length = 20)
    private String merchantUid;
    @Basic
    @Column(name = "pay_method", nullable = false, length = 10)
    private String payMethod;
    @Basic
    @Column(name = "paid_amount", nullable = false)
    private int paidAmount;
    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentState status;
    @Basic
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Basic
    @Column(name = "pg_provider", nullable = false, length = 50)
    private String pgProvider;
    @Basic
    @Column(name = "emb_pg_provider", nullable = true, length = 50)
    private String embPgProvider;
    @Basic
    @Column(name = "pg_tid", nullable = false, length = 50)
    private String pgTid;
    @Basic
    @Column(name = "buyer_name", nullable = false, length = 20)
    private String buyerName;
    @Basic
    @Column(name = "buyer_email", nullable = false, length = 300)
    private String buyerEmail;
    @Basic
    @Column(name = "buyer_tel", nullable = false, length = 20)
    private String buyerTel;
    @Basic
    @Column(name = "buyer_address", nullable = false, length = 100)
    private String buyerAddress;
    @Basic
    @Column(name = "paid_at", nullable = false)
    private Timestamp paidAt;
    @Basic
    @Column(name = "receipt_url", nullable = false, length = 255)
    private String receiptUrl;
    @Basic
    @Column(name = "apply_num", nullable = true, length = 30)
    private String applyNum;
    @Basic
    @Column(name = "vbank_num", nullable = true, length = 30)
    private String vbankNum;
    @Basic
    @Column(name = "vbank_name", nullable = true, length = 10)
    private String vbankName;
    @Basic
    @Column(name = "vbank_code", nullable = true, length = 10)
    private String vbankCode;
    @Basic
    @Column(name = "vbank_holder", nullable = true, length = 10)
    private String vbankHolder;
    @Basic
    @Column(name = "vbank_date", nullable = false)
    private Timestamp vbankDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getImpUid() {
        return impUid;
    }

    public void setImpUid(String impUid) {
        this.impUid = impUid;
    }

    public String getMerchantUid() {
        return merchantUid;
    }

    public void setMerchantUid(String merchantUid) {
        this.merchantUid = merchantUid;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public int getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(int paidAmount) {
        this.paidAmount = paidAmount;
    }

    public PaymentState getStatus() {
        return status;
    }

    public void setStatus(PaymentState status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPgProvider() {
        return pgProvider;
    }

    public void setPgProvider(String pgProvider) {
        this.pgProvider = pgProvider;
    }

    public String getEmbPgProvider() {
        return embPgProvider;
    }

    public void setEmbPgProvider(String embPgProvider) {
        this.embPgProvider = embPgProvider;
    }

    public String getPgTid() {
        return pgTid;
    }

    public void setPgTid(String pgTid) {
        this.pgTid = pgTid;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getBuyerTel() {
        return buyerTel;
    }

    public void setBuyerTel(String buyerTel) {
        this.buyerTel = buyerTel;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public Timestamp getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Timestamp paidAt) {
        this.paidAt = paidAt;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public String getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(String applyNum) {
        this.applyNum = applyNum;
    }

    public String getVbankNum() {
        return vbankNum;
    }

    public void setVbankNum(String vbankNum) {
        this.vbankNum = vbankNum;
    }

    public String getVbankName() {
        return vbankName;
    }

    public void setVbankName(String vbankName) {
        this.vbankName = vbankName;
    }

    public String getVbankHolder() {
        return vbankHolder;
    }

    public void setVbankHolder(String vbankHolder) {
        this.vbankHolder = vbankHolder;
    }

    public Timestamp getVbankDate() {
        return vbankDate;
    }

    public void setVbankDate(Timestamp vbankDate) {
        this.vbankDate = vbankDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payments payments = (Payments) o;
        return id == payments.id &&
                paidAmount == payments.paidAmount &&
                Objects.equals(orderId, payments.orderId) &&
                Objects.equals(impUid, payments.impUid) &&
                Objects.equals(merchantUid, payments.merchantUid) &&
                Objects.equals(payMethod, payments.payMethod) &&
                Objects.equals(status, payments.status) &&
                Objects.equals(name, payments.name) &&
                Objects.equals(pgProvider, payments.pgProvider) &&
                Objects.equals(embPgProvider, payments.embPgProvider) &&
                Objects.equals(pgTid, payments.pgTid) &&
                Objects.equals(buyerName, payments.buyerName) &&
                Objects.equals(buyerEmail, payments.buyerEmail) &&
                Objects.equals(buyerTel, payments.buyerTel) &&
                Objects.equals(buyerAddress, payments.buyerAddress) &&
                Objects.equals(paidAt, payments.paidAt) &&
                Objects.equals(receiptUrl, payments.receiptUrl) &&
                Objects.equals(applyNum, payments.applyNum) &&
                Objects.equals(vbankNum, payments.vbankNum) &&
                Objects.equals(vbankName, payments.vbankName) &&
                Objects.equals(vbankHolder, payments.vbankHolder) &&
                Objects.equals(vbankDate, payments.vbankDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                orderId,
                impUid,
                merchantUid,
                payMethod,
                paidAmount,
                status,
                name,
                pgProvider,
                embPgProvider,
                pgTid,
                buyerName,
                buyerEmail,
                buyerTel,
                buyerAddress,
                paidAt,
                receiptUrl,
                applyNum,
                vbankNum,
                vbankName,
                vbankHolder,
                vbankDate);
    }
}
