package com.matsinger.barofishserver.payment;

public enum PaymentState {
    READY, // ready(브라우저 창 이탈, 가상계좌 발급 완료 등 미결제 상태)
    PAID, // paid(결제완료)
    FAILED; // failed(신용카드 한도 초과, 체크카드 잔액 부족, 브라우저 창 종료 또는 취소 버튼 클릭 등 결제실패 상태)

    public static PaymentState toPaymentState(String paymentState) {
        if (paymentState.equals("paid")) {
            return PAID;
        }
        if (paymentState.equals("ready")) {
            return READY;
        }
        return FAILED;
    }
}
