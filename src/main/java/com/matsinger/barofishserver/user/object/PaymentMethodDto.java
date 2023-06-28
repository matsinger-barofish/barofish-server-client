package com.matsinger.barofishserver.user.object;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import lombok.*;
import org.checkerframework.checker.units.qual.A;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodDto {
    private int id;
    private int userId;
    private String name;
    private String cardName;
    private String cardNo;
    private String expiryAt;
    private String birth;
}
