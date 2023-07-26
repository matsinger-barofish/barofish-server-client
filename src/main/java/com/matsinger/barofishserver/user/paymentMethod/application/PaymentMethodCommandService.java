package com.matsinger.barofishserver.user.paymentMethod.application;

import com.matsinger.barofishserver.payment.dto.CheckValidCardRes;
import com.matsinger.barofishserver.payment.portone.application.PortOneCallbackService;
import com.matsinger.barofishserver.user.dto.AddPaymentMethodReq;
import com.matsinger.barofishserver.user.paymentMethod.domain.PaymentMethod;
import com.matsinger.barofishserver.user.paymentMethod.repository.PaymentMethodRepository;
import com.matsinger.barofishserver.utils.AES256;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.RegexConstructor;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.BillingCustomerData;
import com.siot.IamportRestClient.response.BillingCustomer;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PaymentMethodCommandService {

    private final PaymentMethodRepository paymentMethodRepository;

    private final PortOneCallbackService callbackService;

    private final Common util;
    private final AES256 aes256;
    private final RegexConstructor re;
    @Value("${iamport.credentials.mid}")
    public String mid;

    public PaymentMethod addPaymentMethod(AddPaymentMethodReq request, int userId) throws Exception {

        String name = util.validateString(request.getName(), 20L, "이름");

        String AdjustedCardNo = request.getCardNo().replaceAll(re.cardNo, "$1$2$3$4");
        String hashedCardNo = aes256.encrypt(AdjustedCardNo);
        validateCardInfo(request, userId, hashedCardNo);

        if (request.getPasswordTwoDigit() == null) {
            throw new IllegalArgumentException("비밀번호 두자리를 입력해주세요.");
        }
        if (!Pattern.matches(re.cardPassword, request.getPasswordTwoDigit())) {
            throw new IllegalArgumentException("비밀번호는 숫자 2자리입니다.");
        }
        String password2Digit = aes256.encrypt(request.getPasswordTwoDigit());

        PaymentMethod
                paymentMethod =
                PaymentMethod.builder().name(name).cardNo(hashedCardNo).userId(userId).expiryAt(request.getExpiryAt()).birth(
                        request.getBirth()).passwordTwoDigit(password2Digit).build();

        CheckValidCardRes validCardRes = checkValidCard(paymentMethod);
        if (validCardRes == null) {
            throw new IllegalArgumentException("유효하지 않은 카드입니다.");
        }
        paymentMethod.setCardName(validCardRes.getCardName());
        paymentMethod.setCustomerUid(validCardRes.getCustomerUid());
        return paymentMethodRepository.save(paymentMethod);
    }

    private void validateCardInfo(AddPaymentMethodReq request, int userId, String hashedCardNo) {
        if (request.getCardNo() == null) {
            throw new IllegalArgumentException("카드번호를 입력해주세요.");
        }
        if (!Pattern.matches(re.cardNo, request.getCardNo())) {
            throw new IllegalArgumentException("카드번호 형식을 확인해주세요.");
        }
        if (paymentMethodRepository.existsByCardNoAndUserId(hashedCardNo, userId)) {
            throw new IllegalArgumentException("이미 등록된 카드입니다.");
        }
        if (request.getExpiryAt() == null) {
            throw new IllegalArgumentException("유효기간(월/년) 입력해주세요.");
        }
        if (!Pattern.matches(re.expiryAt, request.getExpiryAt())) {
            throw new IllegalArgumentException("유효기간 형식을 확인해주세요.");
        }
        if (request.getBirth() == null) {
            throw new IllegalArgumentException("생년월일을 입력해주세요");
        }
        if (!Pattern.matches(re.birth, request.getBirth())) {
            throw new IllegalArgumentException("생년월일 형식을 확인해주세요.");
        }
    }

    private CheckValidCardRes checkValidCard(PaymentMethod paymentMethod) throws Exception {
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
        billingCustomerData.setPg("settle");
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
}
