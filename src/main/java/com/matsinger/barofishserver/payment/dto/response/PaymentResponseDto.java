package com.matsinger.barofishserver.payment.dto.response;

import com.matsinger.barofishserver.payment.PaymentState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {

    private String orderId;

    private String impUid; // 포트원 고유 결제 번호

    private String merchantUid; // 주문번호

    private String payMethod; // 결제수단 구분 코드

    private int paidAmount;

    private PaymentState status; // 결제 상태

    private String name; // 주문 이름

    private String pgProvider;  // pg사 구분코드

    private String embPgProvider; // 간편결제 구분코드

    private String pgTid; // PG사에서 거래당 고유하게 부여하는 거래번호

    private String buyerName; // 주문자명

    private String buyerEmail;

    private String buyerTel;

    private String buyerAddr; // 주문자 우편번호

    private Timestamp paidAt; // 결제 승인 시각 (UNIX timestamp)

    private String receiptUrl; // 거래 매출전표 url

    private String applyNum; // 신용카드 승인번호

    private String vbankNum; // 가상계좌 입금 계좌번호

    private String vbankName; // 가상계좌 입금은행 명

    private String vbankHolder; // 가상계좌 예금주

    private Timestamp vbankDate; // 가상계좌 입금기한 (UNIX timestamp)
}
