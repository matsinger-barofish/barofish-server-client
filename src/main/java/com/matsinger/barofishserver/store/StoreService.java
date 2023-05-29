package com.matsinger.barofishserver.store;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class StoreService {

    private final StoreRepository storeRepository;

    private final StoreInfoRepository storeInfoRepository;

    public Optional<Store> selectStoreOptional(Integer id) {
        try {
            return storeRepository.findById(id);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public StoreInfo selectStore(Integer id) {
        return storeInfoRepository.findById(id).orElseThrow(() -> {
            throw new Error("상점 정보를 찾을 수 없습니다.");
        });
    }

    public List<Store> selectStoreList(Boolean isAdmin){
        if(isAdmin){
            return storeRepository.findAll();
        }else{
            return storeRepository.findAllByStateEquals(StoreState.ACTIVE);
        }
    }
    public List<StoreInfo> selectStoreInfoList() {
        return storeInfoRepository.findAll();
    }

    public Boolean checkStoreLoginIdValid(String loginId) {
        try {
            Optional<Store> store = storeRepository.findByLoginId(loginId);
            if (store.isPresent()) return false;
            else return true;
        } catch (Error e) {
            return true;
        }
    }

    public Store addStore(Store data) {
        return storeRepository.save(data);
    }

    public StoreInfo addStoreInfo(StoreInfo data) {
        return storeInfoRepository.save(data);
    }

    public Store updateStore(Store data) {
        return storeRepository.save(data);
    }

    public StoreInfo updateStoreInfo(StoreInfo data) {
        return storeInfoRepository.save(data);
    }

}
