package com.matsinger.barofishserver.domain.user.paymentMethod.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_method", schema = "barofish_dev", catalog = "")
public class PaymentMethod {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Basic
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Basic
    @Column(name = "card_name", nullable = false, length = 50)
    private String cardName;

    @Basic
    @Column(name = "card_no", nullable = false, length = 300)
    private String cardNo;

    @Basic
    @Column(name = "expiry_at", nullable = false, length = 5)
    private String expiryAt;

    @Basic
    @Column(name = "birth", nullable = false, length = 6)
    private String birth;

    @Basic
    @Column(name = "password_two_digit", nullable = false, length = 2)
    private String passwordTwoDigit;

    @Basic
    @Column(name = "customer_uid", nullable = false, length = 100)
    private String customerUid;
}
