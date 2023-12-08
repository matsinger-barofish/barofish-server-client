package com.matsinger.barofishserver.domain.store.application;

import com.matsinger.barofishserver.domain.store.domain.Store;
import com.matsinger.barofishserver.domain.store.dto.StoreExcelInquiryDto;
import com.matsinger.barofishserver.domain.store.repository.StoreQueryRepository;
import com.matsinger.barofishserver.domain.store.repository.StoreRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
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
}
