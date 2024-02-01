package com.matsinger.barofishserver.domain.admin.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
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

    public void setAccessUser(boolean canAccess) {
        this.accessUser = canAccess;
    }

    public void setAccessProduct(boolean canAccess) {
        this.accessProduct = canAccess;
    }

    public void setAccessOrder(boolean canAccess) {
        this.accessOrder = canAccess;
    }

    public void setAccessSettlement(boolean canAccess) {
        this.accessSettlement = canAccess;
    }

    public void setAccessBoard(boolean canAccess) {
        this.accessBoard = canAccess;
    }

    public void setAccessPromotion(boolean canAccess) {
        this.accessPromotion = canAccess;
    }

    public void setAccessSetting(boolean canAccess) {
        this.accessSetting = canAccess;
    }
}
