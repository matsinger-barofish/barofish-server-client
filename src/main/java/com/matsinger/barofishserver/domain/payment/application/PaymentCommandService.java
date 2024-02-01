package com.matsinger.barofishserver.domain.payment.application;

import com.matsinger.barofishserver.domain.order.domain.OrderPaymentWay;
import com.matsinger.barofishserver.domain.order.domain.Orders;
import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.payment.domain.Payments;
import com.matsinger.barofishserver.domain.payment.portone.application.PortOneCallbackService;
import com.matsinger.barofishserver.domain.payment.repository.PaymentRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentCommandService {

    private final PortOneCallbackService portOneCallbackService;
    private final PaymentRepository paymentRepository;

    public void cancelPayment(Orders order, List<OrderProductInfo> orderProductInfos, int additionalDeliveryFee) throws IamportResponseException, IOException {
        int taxFreeAmount = 0;
        int totalPrice = 0;
        for (OrderProductInfo orderProductInfo : orderProductInfos) {
            taxFreeAmount += orderProductInfo.getTaxFreeAmount();
            totalPrice += orderProductInfo.getPrice();
        }

        // 조건부 무료배송을 만족할 경우 취소하려는 상품 금액 전액 취소,
        // 조건부 무료배송을 만족하지 못하는 경우 (취소하려는 상품 금액 - 택배비) 금액 취소
        CancelData cancelData = new CancelData(
                order.getImpUid(),
                true,
                BigDecimal.valueOf(totalPrice - additionalDeliveryFee));
        cancelData.setTax_free(BigDecimal.valueOf(taxFreeAmount));

        if (order.getPaymentWay().equals(OrderPaymentWay.VIRTUAL_ACCOUNT)) {
            cancelData.setRefund_holder(order.getBankHolder());
            cancelData.setRefund_bank(order.getBankCode());
            cancelData.setRefund_account(order.getBankAccount());
        }

        IamportClient iamportClient = portOneCallbackService.getIamportClient();
        IamportResponse<Payment> cancelResult = iamportClient.cancelPaymentByImpUid(cancelData);
        if (cancelResult.getCode() != 0) {
            System.out.println(cancelResult.getMessage());
            throw new BusinessException("환불에 실패하였습니다.");
        }
    }

    public void save(Payments payments) {
        paymentRepository.save(payments);
    }
}
