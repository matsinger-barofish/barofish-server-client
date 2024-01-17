package com.matsinger.barofishserver.domain.payment.application;

import com.matsinger.barofishserver.domain.payment.domain.PaymentState;
import com.matsinger.barofishserver.domain.payment.domain.Payments;
import com.matsinger.barofishserver.domain.order.dto.VBankRefundInfo;
import com.matsinger.barofishserver.domain.payment.dto.GetVBankAccountReq;
import com.matsinger.barofishserver.domain.payment.dto.IamPortCertificationRes;
import com.matsinger.barofishserver.domain.payment.dto.KeyInPaymentReq;
import com.matsinger.barofishserver.domain.payment.portone.application.PortOneCallbackService;
import com.matsinger.barofishserver.domain.payment.repository.PaymentRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.utils.AES256;
import com.matsinger.barofishserver.utils.RegexConstructor;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.*;
import com.siot.IamportRestClient.response.Certification;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PortOneCallbackService callbackService;
    private final AES256 aes256;
    private final RegexConstructor re;
    @Value("${iamport.webhook.url}")
    private String webhookUrl;

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

    public Payments getPaymentInfoFromPortOne(String orderId, String impUid) {
        IamportClient iamportClient = callbackService.getIamportClient();

        IamportResponse<Payment> paymentResponse = null;
        try {
            paymentResponse = iamportClient.paymentByImpUid(impUid);
        } catch (Exception e) {
            throw new BusinessException("결제 정보를 가져오는데 실패했습니다.");
        }

        Payment payment = paymentResponse.getResponse();

        return Payments.builder()
                .orderId(orderId)
                .impUid(impUid)
                .merchantUid(payment.getMerchantUid())
                .payMethod(payment.getPayMethod())
                .paidAmount(payment.getAmount().intValue())
                .status(str2PaymentState(payment.getStatus()))
                .name(payment.getName() != null ? payment.getName() : null)
                .pgProvider(payment.getPgProvider() != null ? payment.getPgProvider() : null)
                .embPgProvider(payment.getEmbPgProvider() != null ? payment.getEmbPgProvider() : null)
                .pgTid(payment.getPgTid() != null ? payment.getPgTid() : null)
                .buyerName(payment.getBuyerName() != null ? payment.getBuyerName() : null)
                .buyerEmail(payment.getBuyerEmail() != null ? payment.getBuyerEmail() : null)
                .buyerTel(payment.getBuyerTel() != null ? payment.getBuyerTel() : null)
                .buyerAddress(payment.getBuyerAddr() != null ? payment.getBuyerAddr() : null)
                .paidAt(payment.getPaidAt() != null ? Timestamp.from(payment.getPaidAt().toInstant()) : null)
                .receiptUrl(payment.getReceiptUrl() != null ? payment.getReceiptUrl() : null)
                .applyNum(payment.getApplyNum() != null ? payment.getApplyNum() : null)
                .vbankNum(payment.getVbankNum() != null ? payment.getVbankNum() : null)
                .vbankCode(payment.getVbankCode() != null ? payment.getVbankCode() : null)
                .vbankName(payment.getVbankName() != null ? payment.getVbankName() : null)
                .vbankHolder(payment.getVbankHolder() != null ? payment.getVbankHolder() : null)
                .vbankDate(payment.getVbankDate() != null ? Timestamp.from(payment.getVbankDate().toInstant()) : null)
                .build();
    }

    public void cancelPayment(String impUid, Integer amount, Integer taxFreeAmount, VBankRefundInfo vBankRefundInfo) throws IamportResponseException, IOException {
        IamportClient iamportClient = callbackService.getIamportClient();
        CancelData
                cancelData =
                amount != null
                        ? new CancelData(impUid, true, BigDecimal.valueOf(amount))
                        : new CancelData(impUid, true);
        if (taxFreeAmount != null) cancelData.setTax_free(BigDecimal.valueOf(taxFreeAmount));
        if (vBankRefundInfo != null) {
            cancelData.setRefund_holder(vBankRefundInfo.getBankHolder());
            cancelData.setRefund_bank(vBankRefundInfo.getBankCode());
            cancelData.setRefund_account(vBankRefundInfo.getBankAccount());
        }
        IamportResponse<Payment> cancelResult = iamportClient.cancelPaymentByImpUid(cancelData);
        if (cancelResult.getCode() != 0) {
            System.out.println(cancelResult.getMessage());
            throw new BusinessException("환불에 실패하였습니다.");
        }
    }

    public IamPortCertificationRes certificateWithImpUid(String impUid) throws Exception {
        IamportClient iamportClient = callbackService.getIamportClient();
        IamportResponse<Certification> certificationRes = iamportClient.certificationByImpUid(impUid);
        if (certificationRes.getCode() != 0) {
            System.out.println("PortOne Certification Error : " + certificationRes.getMessage());
            return null;
        }
        Certification certification = certificationRes.getResponse();
        return IamPortCertificationRes.builder().impUid(certification.getImpUid()).name(certification.getName()).phone(
                certification.getPhone()).certified(certification.isCertified()).certifiedAt(certification.getCertifiedAt().toString()).build();
    }

    public Boolean processKeyInPayment(KeyInPaymentReq data) throws IamportResponseException, IOException {
        IamportClient iamportClient = callbackService.getIamportClient();
        String cardNo = aes256.decrypt(data.getPaymentMethod().getCardNo());
        cardNo = cardNo.replaceAll(re.cardNo, "$1-$2-$3-$4");
        String[] expiryAyData = data.getPaymentMethod().getExpiryAt().split("/");
        String expiryMonth = expiryAyData[0];
        String expiryYear = "20" + expiryAyData[1];
        String expiry = expiryYear + "-" + expiryMonth;
        String password2Digit = aes256.decrypt(data.getPaymentMethod().getPasswordTwoDigit());
        CardInfo cardInfo = new CardInfo(cardNo, expiry, data.getPaymentMethod().getBirth(), password2Digit);
        AgainPaymentData
                againPaymentData =
                new AgainPaymentData(data.getPaymentMethod().getCustomerUid(),
                        data.getOrderId(),
                        BigDecimal.valueOf(data.getTotal_amount()));
        againPaymentData.setTaxFree(BigDecimal.valueOf(data.getTaxFree()));
        againPaymentData.setName(data.getOrder_name());
        againPaymentData.setNoticeUrl(webhookUrl);
        IamportResponse<Payment> paymentRes = iamportClient.againPayment(againPaymentData);
        if (paymentRes.getCode() != 0) {
            System.out.println(paymentRes.getMessage());
            return false;
        }
        return true;
    }

    public Payments findPaymentByImpUid(String impUid) {
        return paymentRepository.findFirstByImpUid(impUid);
    }

    public void upsertPayments(Payments payments) {
        log.info("payments 저장");
        paymentRepository.save(payments);
    }

    public void getVBankAccount(GetVBankAccountReq data) throws IamportResponseException, IOException {
        IamportClient iamportClient = callbackService.getIamportClient();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.iamport.kr/vbanks";
        String accessToken = iamportClient.getAuth().getResponse().getToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("merchant_uid", data.getOrderId());
        body.add("amount", data.getPrice());
        body.add("vbank_code", data.vBankCode);
        body.add("vbank_due", data.vBankDue);
        body.add("vbank_holder", data.vBankHolder);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }


    public void save(Payments payments) {
        paymentRepository.save(payments);
    }
}
