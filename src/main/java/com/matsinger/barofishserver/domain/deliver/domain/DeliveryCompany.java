package com.matsinger.barofishserver.domain.deliver.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delivery_company", schema = "barofish_dev", catalog = "")
public class DeliveryCompany {
    @Id
    @Column(name = "code", nullable = false)
    private String code;

    @Basic
    @Column(name = "name", nullable = false, length = 50)
    private String name;

}
