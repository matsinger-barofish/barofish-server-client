package com.matsinger.barofishserver.domain.settlement.application;

import com.matsinger.barofishserver.domain.settlement.domain.Settlement;
import com.matsinger.barofishserver.domain.settlement.dto.SettlementDto;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.domain.store.domain.StoreInfo;
import com.matsinger.barofishserver.domain.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class SettlementCommandService {
    private final SettlementRepository settlementRepository;
    private final StoreService storeService;
    public void addSettlement(Settlement settlement) {
        settlementRepository.save(settlement);
    }

    public Settlement updateSettlement(Settlement settlement) {
        return settlementRepository.save(settlement);
    }
    public SettlementDto convert2Dto(Settlement s) {
        StoreInfo storeInfo = storeService.selectStoreInfo(s.getStoreId());
        return SettlementDto.builder().id(s.getId()).storeId(s.getStoreId()).storeName(storeInfo.getName()).settlementAmount(
                s.getSettlementAmount()).settledAt(s.getSettledAt()).cancelReason(s.getCancelReason()).build();
    }
}
