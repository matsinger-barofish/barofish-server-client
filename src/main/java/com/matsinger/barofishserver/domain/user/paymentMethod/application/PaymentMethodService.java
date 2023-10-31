package com.matsinger.barofishserver.domain.user.paymentMethod.application;

import com.matsinger.barofishserver.domain.user.paymentMethod.domain.PaymentMethod;
import com.matsinger.barofishserver.domain.user.paymentMethod.dto.PaymentMethodDto;
import com.matsinger.barofishserver.domain.user.paymentMethod.repository.PaymentMethodRepository;
import com.matsinger.barofishserver.utils.AES256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final AES256 aes256;


    public PaymentMethod addPaymentMethod(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    public PaymentMethod updatePayment(PaymentMethod paymentMethod) {
        return paymentMethodRepository.save(paymentMethod);
    }

    public PaymentMethod selectPaymentMethod(Integer id) {
        return paymentMethodRepository.findById(id).orElseThrow(() -> {
            throw new Error("결제 수단 정보를 찾을 수 없습니다.");
        });
    }

    public List<PaymentMethod> selectPaymentMethodList(Integer userId) {
        return paymentMethodRepository.findAllByUserId(userId);
    }

    public Boolean checkExistPaymentWithCardNo(String cardNo, Integer userId) {
        return paymentMethodRepository.existsByCardNoAndUserId(cardNo, userId);
    }

    public void deletePaymentMethod(Integer id) {
        paymentMethodRepository.deleteById(id);
    }

    public PaymentMethodDto convert2Dto(PaymentMethod paymentMethod) throws Exception {
        return PaymentMethodDto.builder().id(paymentMethod.getId()).userId(paymentMethod.getUserId()).cardNo(aes256.decrypt(
                paymentMethod.getCardNo()).replaceAll("(\\d{6})(\\d{6})(\\d{3})(\\d)", "$1******$3*")).expiryAt(
                paymentMethod.getExpiryAt()).birth(paymentMethod.getBirth()).cardName(paymentMethod.getCardName()).name(
                paymentMethod.getName()).build();
    }
}
