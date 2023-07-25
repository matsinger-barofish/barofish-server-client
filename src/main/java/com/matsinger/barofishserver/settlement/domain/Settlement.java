package com.matsinger.barofishserver.settlement.domain;

import com.matsinger.barofishserver.settlement.dto.SettlementDto;
import com.matsinger.barofishserver.store.domain.Store;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "settlement", schema = "barofish_dev", catalog = "")
public class Settlement {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "store_id", nullable = false)
    private int storeId;

    @ManyToOne
    @JoinColumn(name = "store_id", insertable = false, updatable = false)
    private Store store;

    @Basic
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private SettlementState state;

    @Basic
    @Column(name = "settlement_amount")
    private int settlementAmount;

    @Basic
    @Column(name = "settled_at")
    private Timestamp settledAt;

    @Basic
    @Column(name = "cancel_reason")
    private String cancelReason;

    public SettlementDto convert2Dto() {
        return SettlementDto.builder().id(this.id).storeId(this.storeId).state(this.state).settlementAmount(this.settlementAmount).settledAt(
                this.settledAt).cancelReason(this.cancelReason).build();
    }
}
