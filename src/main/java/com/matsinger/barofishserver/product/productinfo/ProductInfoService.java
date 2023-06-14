package com.matsinger.barofishserver.product.productinfo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductInfoService {
    private final ProductLocationRepository locationRepository;
    private final ProductProcessRepository processRepository;
    private final ProductStorageRepository storageRepository;
    private final ProductTypeRepository typeRepository;
    private final ProductUsageRepository usageRepository;

    public List<ProductLocation> selectProductLocationList() {
        return locationRepository.findAll();
    }

    public ProductLocation selectProductLocation(Integer id) {
        return locationRepository.findById(id).orElseThrow(() -> {
            throw new Error("지역 정보를 찾을 수 없습니다.");
        });
    }

    public List<ProductProcess> selectProductProcessList() {
        return processRepository.findAll();
    }

    public ProductProcess selectProductProcess(Integer id) {
        return processRepository.findById(id).orElseThrow(() -> {
            throw new Error("가공 정보를 찾을 수 없습니다.");
        });
    }

    public List<ProductStorage> selectProductStorageList() {
        return storageRepository.findAll();
    }

    public ProductStorage selectProductStorage(Integer id) {
        return storageRepository.findById(id).orElseThrow(() -> {
            throw new Error("보관 정보를 찾을 수 없습니다.");
        });
    }

    public List<ProductType> selectProductTypeList() {
        return typeRepository.findAll();
    }

    public ProductType selectProductType(Integer id) {
        return typeRepository.findById(id).orElseThrow(() -> {
            throw new Error("구분 정보를 찾을 수 없습니다.");
        });
    }

    public List<ProductUsage> selectProductUsageList() {
        return usageRepository.findAll();
    }

    public ProductUsage selectProductUsage(Integer id) {
        return usageRepository.findById(id).orElseThrow(() -> {
            throw new Error("용도 정보를 찾을 수 없습니다.");
        });
    }
}
