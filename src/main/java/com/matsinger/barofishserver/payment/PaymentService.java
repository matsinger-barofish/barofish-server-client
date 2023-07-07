package com.matsinger.barofishserver.payment;

import com.matsinger.barofishserver.user.object.PaymentMethod;
import com.matsinger.barofishserver.utils.AES256;
import com.matsinger.barofishserver.utils.RegexConstructor;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.*;
import com.siot.IamportRestClient.response.BillingCustomer;
import com.siot.IamportRestClient.response.Certification;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentService {
    @Value("${iamport.webhook.url}")
    private String webhookUrl;
    private final PaymentRepository paymentRepository;
    private final PortOneCallbackService callbackService;
    private final AES256 aes256;
    private final RegexConstructor re;

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
        return Payments.builder().orderId(orderId).impUid(impUid).merchantUid(payment.getMerchantUid()).payMethod(
                payment.getPayMethod()).paidAmount(payment.getAmount().intValue()).status(str2PaymentState(
                payment.getStatus())).name(payment.getName()).pgProvider(payment.getPgProvider()).embPgProvider(
                payment.getEmbPgProvider()).pgTid(payment.getPgTid()).buyerName(payment.getBuyerName()).buyerEmail(
                payment.getBuyerEmail()).buyerTel(payment.getBuyerTel()).buyerAddress(payment.getBuyerAddr()).paidAt(
                Timestamp.from(payment.getPaidAt().toInstant())).receiptUrl(payment.getReceiptUrl()).applyNum(
                payment.getApplyNum()).vbankNum(payment.getVbankNum()).vbankName(payment.getVbankName()).vbankHolder(
                payment.getVbankHolder()).vbankDate(Timestamp.from(payment.getVbankDate().toInstant())).build();
    }

    public void cancelPayment(String impUid, Integer amount) throws IamportResponseException, IOException {
        IamportClient iamportClient = callbackService.getIamportClient();
        CancelData
                cancelData =
                amount != null ? new CancelData(impUid, true, BigDecimal.valueOf(amount)) : new CancelData(impUid,
                        true);
        iamportClient.cancelPaymentByImpUid(cancelData);
    }

    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyInPaymentReq {
        String order_name;
        Integer total_amount;
        String orderId;
        PaymentMethod paymentMethod;
    }

    private static class KeyInPaymentRes {
        String tx_id;
        String customer_id;
        String requested_at;
        String paid_at;
        String pg_tx_id;
    }

    public Boolean keyInPayment(KeyInPaymentReq data) throws IamportResponseException, IOException {
//        IamportClient iamportClient = callbackService.getIamportClient();
//        String accessToken = iamportClient.getAuth().getResponse().getToken();
//        RestTemplate restTemplate = new RestTemplate();
//        String url = "https://api.portone.io/v2/payments/" + data.orderId + "/instant/pay";
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + accessToken);
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        MultiValueMap<String, String> cardCredential = new LinkedMultiValueMap<>();
//        cardCredential.add("card_number", data.card_number);
//        cardCredential.add("expiry_month", data.expiry_month);
//        cardCredential.add("expiry_year", data.expiry_year);
//        cardCredential.add("birth_or_business_registration_number", data.birth);
//        cardCredential.add("password_two_digits", data.password2digits);
//        body.add("order_name", data.order_name);
//        body.add("total_amount", data.total_amount);
//        body.add("currency", "KRW");
//        body.add("notice_urls", new ArrayList<String>(Arrays.asList(callbackService.callbackUrl)));
//        body.add("channel_name", callbackService.channelName);
//        body.add("payment_method_option", cardCredential);
//        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return true;
    }

    @Getter
    @Builder
    public static class IamPortCertificationRes {
        String impUid;
        String name;
        String phone;
        Boolean certified;
        String certifiedAt;
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

    @Getter
    @Builder
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckValidCardRes {
        String customerUid;
        String cardName;
    }

    public CheckValidCardRes checkValidCard(PaymentMethod paymentMethod) throws Exception {
        IamportClient iamportClient = callbackService.getIamportClient();
        String customerUid = "customer_" + paymentMethod.getUserId() + "_" + paymentMethod.getId();
        String cardNo = aes256.decrypt(paymentMethod.getCardNo());
        cardNo = cardNo.replaceAll(re.cardNo, "$1-$2-$3-$4");
        String[] expiryAyData = paymentMethod.getExpiryAt().split("/");
        String expiryMonth = expiryAyData[0];
        String expiryYear = "20" + expiryAyData[1];
        String expiry = expiryYear + "-" + expiryMonth;
        String birth = paymentMethod.getBirth();
        BillingCustomerData billingCustomerData = new BillingCustomerData(customerUid, cardNo, expiry, birth);
        billingCustomerData.setPwd2Digit(aes256.decrypt(paymentMethod.getPasswordTwoDigit()));
        IamportResponse<BillingCustomer>
                billingCustomerRes =
                iamportClient.postBillingCustomer(customerUid, billingCustomerData);
        if (billingCustomerRes.getCode() != 0) {
            System.out.println(billingCustomerRes.getCode() + ": " + billingCustomerRes.getMessage());
            return null;
        }
        BillingCustomer billingCustomer = billingCustomerRes.getResponse();
        return CheckValidCardRes.builder().cardName(billingCustomer.getCardName()).customerUid(billingCustomer.getCustomerUid()).build();
    }

    public Boolean processKeyInPayment(KeyInPaymentReq data) throws Exception {
        IamportClient iamportClient = callbackService.getIamportClient();
        String cardNo = aes256.encrypt(data.paymentMethod.getCardNo());
        cardNo = cardNo.replaceAll(re.cardNo, "$1-$2-$3-$4");
        String[] expiryAyData = data.paymentMethod.getExpiryAt().split("/");
        String expiryMonth = expiryAyData[0];
        String expiryYear = "20" + expiryAyData[1];
        String expiry = expiryYear + "-" + expiryMonth;
        String password2Digit = aes256.decrypt(data.paymentMethod.getPasswordTwoDigit());
        CardInfo cardInfo = new CardInfo(cardNo, expiry, data.paymentMethod.getBirth(), password2Digit);

        OnetimePaymentData
                onetimePaymentData =
                new OnetimePaymentData(data.orderId, BigDecimal.valueOf(data.total_amount), cardInfo);
        AgainPaymentData
                againPaymentData =
                new AgainPaymentData(data.paymentMethod.getCustomerUid(),
                        data.orderId,
                        BigDecimal.valueOf(data.total_amount));
        againPaymentData.setNoticeUrl(webhookUrl);
        IamportResponse<Payment> paymentRes = iamportClient.againPayment(againPaymentData);
//        IamportResponse<Payment> paymentRes = iamportClient.onetimePayment(onetimePaymentData);
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
        paymentRepository.save(payments);
    }


}
