package com.matsinger.barofishserver.payment.dto.request;

import com.matsinger.barofishserver.payment.Payment;
import com.matsinger.barofishserver.payment.PaymentState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 참고: https://developers.portone.io/docs/ko/sdk/javascript-sdk/payrt
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortOnePaymentRequestDto {

//    private boolean success; // 토스페이먼츠 신모듈은 제공 안함
    private String error_code; // 결제 실패 코드
    private String error_msg; // 결제 실패 메시지
    private String imp_uid; // 포트원 고유 결제 번호
    private String merchant_uid; // 주문번호
    private String pay_method; // 결제수단 구분 코드

    private int paid_amount;

    private String status; // 결제 상태
    private String name; // 주문자명

    private String pg_provider;  // pg사 구분코드
    private String emb_pg_provider; // 간편결제 구분코드

    private String pg_tid; // PG사에서 거래당 고유하게 부여하는 거래번호
    private String buyer_name; // 주문자명
    private String buyer_email;
    private String buyer_tel;
    private String buyer_addr; // 주문자 우편번호
    private String custom_data; // 가맹점 임의 지정 데이터
    private String paid_at; // 결제 승인 시각 (UNIX timestamp)
    private String receipt_url; // 거래 매출전표 url

    private String apply_num; // 신용카드 승인번호
    private String vbank_num; // 가상계좌 입금 계좌번호
    private String vbank_name; // 가상계좌 입금은행 명
    private String vbank_holder; // 가상계좌 예금주
    private String vbank_date; // 가상계좌 입금기한 (UNIX timestamp)

    public Payment toEntity() {
        PaymentState paymentState = PaymentState.toPaymentState(status);
        return Payment.builder()
                .impUid(getImp_uid())
                .merchantUid(getMerchant_uid())
                .payMethod(getPay_method())
                .paidAmount(getPaid_amount())
                .status(paymentState)
                .name(getName())
                .pgProvider(pg_provider)
                .embPgProvider(emb_pg_provider)
                .pgTid(pg_tid)
                .buyerName(buyer_name)
                .buyerEmail(buyer_email)
                .buyerTel(buyer_tel)
                .buyerAddr(buyer_addr)
                .customData(custom_data)
                .paidAt(paid_at)
                .receiptUrl(receipt_url)
                .applyNum(apply_num)
                .vbankNum(vbank_num)
                .vbankName(vbank_name)
                .vbankHolder(vbank_holder)
                .vbankDate(vbank_date).build();
    }
}