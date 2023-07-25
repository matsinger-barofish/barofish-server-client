package com.matsinger.barofishserver.settlement.application;

import com.matsinger.barofishserver.order.application.OrderService;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.settlement.domain.Settlement;
import com.matsinger.barofishserver.settlement.domain.SettlementState;
import com.matsinger.barofishserver.settlement.repository.SettlementRepository;
import com.matsinger.barofishserver.store.application.StoreService;
import com.matsinger.barofishserver.store.domain.StoreInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SettlementQueryService {
    private final SettlementRepository settlementRepository;
    private final StoreService storeService;
    private final OrderService orderService;

    public Settlement selectSettlement(Integer id) {
        return settlementRepository.findById(id).orElseThrow(() -> {
            throw new Error("정산 내역 정보를 찾을 수 없습니다.");
        });
    }

    public Page<Settlement> selectSettlementList(Specification<Settlement> spec, Pageable pageable) {
        return settlementRepository.findAll(spec, pageable);
    }

    public List<Settlement> selectSettlementListWithState(SettlementState state) {
        if (state == null) return settlementRepository.findAll();
        else return settlementRepository.findAllByState(state);
    }
    public Integer getSettlementAmount(Integer storeId, Boolean isSettled) {
        List<OrderProductInfo>
                infos =
                orderService.selectOrderProductInfoListWithStoreIdAndIsSettled(storeId, isSettled);
        return getSettlementAmount(infos, storeId);
    }
    public Integer getSettlementAmount(List<OrderProductInfo> infos, Integer storeId) {
        int totalPrice = 0;
        if (infos != null && infos.size() != 0) {
            StoreInfo storeInfo = storeService.selectStoreInfo(storeId);
            Integer
                    settlementRate =
                    storeInfo.getSettlementRate() == null ||
                            storeInfo.getSettlementRate() == 100 ? null : storeInfo.getSettlementRate();
            for (OrderProductInfo info : infos) {
                int salePrice = info.getSettlePrice() != null ? info.getSettlePrice() : 0;
                int
                        price =
                        (settlementRate == null ? salePrice * info.getAmount() : (int) (salePrice * settlementRate /
                                100.) / 10 * 10);
                totalPrice += price;
            }
        }
        return totalPrice;
    }
}
