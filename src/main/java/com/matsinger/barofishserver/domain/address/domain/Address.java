package com.matsinger.barofishserver.domain.address.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address", schema = "barofish_dev", catalog = "")
public class Address {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "hcode", nullable = false, length = 10)
    private String hcode;
    @Basic
    @Column(name = "sido", nullable = false, length = 12)
    private String sido;
    @Basic
    @Column(name = "sigungu", nullable = false, length = 10)
    private String sigungu;
    @Basic
    @Column(name = "hname", nullable = false, length = 12)
    private String hname;
    @Basic
    @Column(name = "bcode", nullable = false, length = 10)
    private String bcode;
    @Basic
    @Column(name = "bname", nullable = false, length = 10)
    private String bname;
}
