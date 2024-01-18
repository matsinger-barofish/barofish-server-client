package com.matsinger.barofishserver.domain.store.application;

import com.matsinger.barofishserver.domain.store.domain.Store;
import com.matsinger.barofishserver.domain.store.domain.StoreRecommendType;
import com.matsinger.barofishserver.domain.store.dto.SimpleStore;
import com.matsinger.barofishserver.domain.store.dto.StoreExcelInquiryDto;
import com.matsinger.barofishserver.domain.store.dto.StoreRecommendInquiryDto;
import com.matsinger.barofishserver.domain.store.repository.StoreQueryRepository;
import com.matsinger.barofishserver.domain.store.repository.StoreRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreQueryService {

    private final StoreRepository storeRepository;
    private final StoreExcelQueryService storeExcelQueryService;
    private final StoreQueryRepository storeQueryRepository;

    public Store findById(int id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("파트너를 찾을 수 없습니다."));
    }

    public Workbook downloadStoresWithExcel(List<Integer> storeIds) {

        List<StoreExcelInquiryDto> excelDtos = storeQueryRepository.getExcelDataByStoreIds(storeIds);

        return storeExcelQueryService.makeExcelForm(excelDtos);
    }

    public List<SimpleStore> selectRecommendStoreList(PageRequest pageRequest, StoreRecommendType type, String keyword, Integer userId) {
        List<StoreRecommendInquiryDto> stores = new ArrayList<>();
        if (type == StoreRecommendType.RECENT) {
            stores = storeQueryRepository.selectRecommendStoreWithJoinAt(pageRequest, keyword, userId);
        }
        if (type == StoreRecommendType.BOOKMARK) {
            stores = storeQueryRepository.selectRecommendStoreWithScrape(pageRequest, keyword, userId);
        }
        if (type == StoreRecommendType.ORDER) {
            stores = storeQueryRepository.selectRecommendStoreWithOrder(pageRequest, keyword, userId);
        }
        if (type == StoreRecommendType.REVIEW) {
            stores = storeQueryRepository.selectRecommendStoreWithReview(pageRequest, keyword, userId);
        }

        List<SimpleStore> response = stores.stream().map(
                v -> v.toDto(v.getKeyword().split(","))
        ).toList();

        return response;
    }
}
