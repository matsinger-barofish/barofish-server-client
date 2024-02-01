package com.matsinger.barofishserver.domain.product.weeksdate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "weeks_date")
public class WeeksDate {

    @Id
    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "is_delivery_company_holiday", nullable = false)
    private boolean isDeliveryCompanyHoliday;

    @Column(name = "description", nullable = false)
    private String description;
}
