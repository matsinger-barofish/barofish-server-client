package com.matsinger.barofishserver.payment;

import com.matsinger.barofishserver.order.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        order.setPayment(this);
    }

    @Column(name = "imp_uid", nullable = false, length = 30)
    private String impUid; // 포트원 고유 결제 번호

    @Column(name = "merchant_uid", nullable = false, length = 20)
    private String merchantUid; // 주문번호

    @Column(name = "pay_method", nullable = false, length = 10)
    private String payMethod; // 결제수단 구분 코드

    @Column(name = "paid_amount", nullable = false)
    private int paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private PaymentState status; // 결제 상태

    @Column(name = "name", nullable = false)
    private String name; // 주문 이름

    @Column(name = "pg_provider", nullable = false, length = 10)
    private String pgProvider;  // pg사 구분코드

    @Column(name = "emb_pg_provider", nullable = true)
    private String embPgProvider; // 간편결제 구분코드

    @Column(name = "pg_tid", nullable = false)
    private String pgTid; // PG사에서 거래당 고유하게 부여하는 거래번호

    @Column(name = "buyer_name", nullable = false, length = 20)
    private String buyerName; // 주문자명

    @Column(name = "buyer_email", nullable = false, length = 30)
    private String buyerEmail;

    @Column(name = "buyer_tel", nullable = false, length = 20)
    private String buyerTel;

    @Column(name = "buyer_address", nullable = false)
    private String buyerAddr; // 주문자 우편번호

//    @Column(name = "custom_data", nullable = false)
//    private String customData; // 가맹점 임의 지정 데이터

    @Column(name = "paid_at", nullable = false)
    private Timestamp paidAt; // 결제 승인 시각 (UNIX timestamp)

    @Column(name = "receipt_url", nullable = false)
    private String receiptUrl; // 거래 매출전표 url

    @Column(name = "apply_num")
    private String applyNum; // 신용카드 승인번호

    @Column(name = "vbank_num")
    private String vbankNum; // 가상계좌 입금 계좌번호

    @Column(name = "vbank_name")
    private String vbankName; // 가상계좌 입금은행 명

    @Column(name = "vbank_holder")
    private String vbankHolder; // 가상계좌 예금주

    @Column(name = "vbank_date")
    private Timestamp vbankDate; // 가상계좌 입금기한 (UNIX timestamp)
}
