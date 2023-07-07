package com.matsinger.barofishserver.settlement;

import com.matsinger.barofishserver.order.OrderService;
import com.matsinger.barofishserver.order.object.OrderProductInfo;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.store.object.StoreInfo;
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
public class SettlementService {
    private final SettlementRepository settlementRepository;
    private final StoreService storeService;
    private final OrderService orderService;
    private final ProductService productService;

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

    public void addSettlement(Settlement settlement) {
        settlementRepository.save(settlement);
    }

    public Settlement updateSettlement(Settlement settlement) {
        return settlementRepository.save(settlement);
    }

    public Integer getSettlementAmount(Integer storeId, Boolean isSettled) {
//        Tuple data = settlementRepository.getNeedSettleAmount(storeId, isSettled);
//        Integer amount = Integer.parseInt(data.get("amount").toString());
//        return amount;
//        List<OrderProductInfo> infos = orderService.
        List<OrderProductInfo>
                infos =
                orderService.selectOrderProductInfoListWithStoreIdAndIsSettled(storeId, isSettled);
        return getSettlementAmount(infos, storeId);
    }

    public SettlementDto convert2Dto(Settlement s) {
        StoreInfo storeInfo = storeService.selectStoreInfo(s.getStoreId());
        return SettlementDto.builder().id(s.getId()).storeId(s.getStoreId()).storeName(storeInfo.getName()).settlementAmount(
                s.getSettlementAmount()).settledAt(s.getSettledAt()).cancelReason(s.getCancelReason()).build();
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
