package com.matsinger.barofishserver.filter;

import com.matsinger.barofishserver.product.productinfo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class FilterService {
    private final ProductLocationRepository locationRepository;
    private final ProductProcessRepository processRepository;
    private final ProductStorageRepository storageRepository;
    private final ProductTypeRepository typeRepository;
    private final ProductUsageRepository usageRepository;

    List<ProductLocation> selectProductLocations() {
        return locationRepository.findAll();
    }

    List<ProductProcess> selectProductProcesses() {
        return processRepository.findAll();
    }

    List<ProductStorage> selectProductStorages() {
        return storageRepository.findAll();
    }

    List<ProductType> selectProductTypes() {
        return typeRepository.findAll();
    }

    List<ProductUsage> selectProductUsages() {
        return usageRepository.findAll();
    }
}
