package com.matsinger.barofishserver.settlement.dto;

import com.matsinger.barofishserver.order.domain.OrderPaymentWay;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSettlementExcelDto {

    private int productId;                          // 상품번호
    private String orderId;                         // 주문번호
    private OrderProductState orderProductState;    // 주문상태
    private Timestamp orderAt;                      // 주문일
    private String storeName;                       // 스토어 이름
    private String productName;                     // 상품명
    private String optionName;                      // 옵션명
    private boolean needTaxation;                   // 과세 여부
    private int purchasePrice;                      // 매입가
    private int originPrice;                        // 정가
    private int discountPrice;                      // 할인가
    private int deliveryFee;                        // 배송비
    private int quantity;                           // 수량
    private int orderAmount;                        // 주문금액
    //    private int finalPaymentAmount;                // 최종 결제금액
    private OrderPaymentWay paymentMethod;          // 결제수단
    private double settlementRatio;                 // 정산비율
    private String couponName;                      // 쿠폰명
    private int couponDiscount;                     // 쿠폰할일
    private int usePoint;                           // 포인트
    private double settlementAmount;                // 정산액 = 할인가 * 수량 / (1 - 정산비율)
    private double finalSettlementAmount;            // 최종정산액 = 정산액 + 배송비
    private Timestamp settledAt;                    // 정산일시
    private String customerName;                    // 주문인
    private String customerPhoneNumber;             // 연락처
    private String customerEmail;                   // 이메일
    private String customerAddress;                 // 배송지
    private String deliveryMessage;                 // 배송메세지
    private String deliveryCompany;                 // 택배사
    private String trackingNumber;                  // 운송장번호
}
