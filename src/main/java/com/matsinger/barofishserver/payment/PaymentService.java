package com.matsinger.barofishserver.payment;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final PortOneCallbackService callbackService;

    public Payments selectPayment(String id) {
        return paymentRepository.findFirstByMerchantUid(id);
    }

    public Payments updatePayment(Payments payment) {
        return paymentRepository.save(payment);
    }

    private PaymentState str2PaymentState(String str) {
        switch (str) {
            case "ready":
                return PaymentState.READY;
            case "paid":
                return PaymentState.PAID;
            case "cancelled":
                return PaymentState.CANCELED;
            case "failed":
                return PaymentState.FAILED;

        }
        return PaymentState.FAILED;
    }

    public Payments getPaymentInfo(String orderId, String impUid) throws IamportResponseException, IOException {
        IamportClient iamportClient = callbackService.getIamportClient();
        IamportResponse<Payment> paymentResponse = iamportClient.paymentByImpUid(impUid);
        Payment payment = paymentResponse.getResponse();
        Payments
                payments =
                Payments.builder().orderId(orderId).impUid(impUid).merchantUid(payment.getMerchantUid()).payMethod(
                        payment.getPayMethod()).paidAmount(payment.getAmount().intValue()).status(str2PaymentState(
                        payment.getStatus())).name(payment.getName()).pgProvider(payment.getPgProvider()).embPgProvider(
                        payment.getEmbPgProvider()).pgTid(payment.getPgTid()).buyerName(payment.getBuyerName()).buyerEmail(
                        payment.getBuyerEmail()).buyerTel(payment.getBuyerTel()).paidAt(Timestamp.from(payment.getPaidAt().toInstant())).receiptUrl(
                        payment.getReceiptUrl()).applyNum(payment.getApplyNum()).vbankNum(payment.getVbankNum()).vbankName(
                        payment.getVbankName()).vbankHolder(payment.getVbankHolder()).vbankDate(Timestamp.from(payment.getVbankDate().toInstant())).build();
        return payments;
    }

    public Boolean cancelPayment(String impUid, Integer amount) throws IamportResponseException, IOException {
        IamportClient iamportClient = callbackService.getIamportClient();
        CancelData
                cancelData =
                amount != null ? new CancelData(impUid, true, BigDecimal.valueOf(amount)) : new CancelData(impUid,
                        true);
        iamportClient.cancelPaymentByImpUid(cancelData);
        return true;
    }

    public Payments findPaymentByImpUid(String impUid) {
        return paymentRepository.findFirstByImpUid(impUid);
    }

    public Payments upsertPayments(Payments payments) {
        return paymentRepository.save(payments);
    }
}
