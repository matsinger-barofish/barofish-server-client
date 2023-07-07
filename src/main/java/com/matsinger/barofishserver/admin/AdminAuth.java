package com.matsinger.barofishserver.admin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAuth {
    @Id
    @Column(name = "admin_id", nullable = false)
    private int adminId;
    @Basic
    @Column(name = "access_user", nullable = false)
    private Boolean accessUser;
    @Basic
    @Column(name = "access_product", nullable = false)
    private Boolean accessProduct;
    @Basic
    @Column(name = "access_order", nullable = false)
    private Boolean accessOrder;
    @Basic
    @Column(name = "access_settlement", nullable = false)
    private Boolean accessSettlement;
    @Basic
    @Column(name = "access_board", nullable = false)
    private Boolean accessBoard;
    @Basic
    @Column(name = "access_promotion", nullable = false)
    private Boolean accessPromotion;
    @Basic
    @Column(name = "access_setting", nullable = false)
    private Boolean accessSetting;
    @OneToOne
    @JoinColumn(name = "admin_id", insertable = false, updatable = false)
    @JsonIgnore
    private Admin admin;
}
