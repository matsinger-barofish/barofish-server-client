package com.matsinger.barofishserver.store;


import com.matsinger.barofishserver.store.object.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class StoreService {

    private final StoreRepository storeRepository;

    private final StoreInfoRepository storeInfoRepository;

    private final StoreScrapRepository storeScrapRepository;

    public StoreDto convert2Dto(Store store){
        StoreInfo storeInfo = selectStoreInfo(store.getId());
        return StoreDto.builder().id(store.getId()).state(store.getState()).loginId(store.getLoginId()).joinAt(store.getJoinAt()).backgroundImage(
                storeInfo.getBackgroudImage()).profileImage(storeInfo.getProfileImage()).name(storeInfo.getName()).location(
                storeInfo.getLocation()).visitNote(storeInfo.getVisitNote()).keyword(storeInfo.getKeyword().split(",")).build();
    }

    public Optional<Store> selectStoreOptional(Integer id) {
        try {
            return storeRepository.findById(id);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public List<SimpleStore> selectRecommendStore(StoreRecommendType type, Integer page, Integer take, String keyword) {
        List<StoreInfo> infos = new ArrayList<>();
        switch (type) {
            case RECENT:
                infos =
                        storeInfoRepository.selectRecommendStoreWithJoinAt(Pageable.ofSize(take).withPage(page),
                                keyword);
                break;
            case BOOKMARK:
                infos =
                        storeInfoRepository.selectRecommendStoreWithScrap(Pageable.ofSize(take).withPage(page),
                                keyword);
                break;
            case ORDER:
                infos =
                        storeInfoRepository.selectRecommendStoreWithOrder(Pageable.ofSize(take).withPage(page),
                                keyword);
                break;
            case REVIEW:
                infos =
                        storeInfoRepository.selectRecommendStoreWithReview(Pageable.ofSize(take).withPage(page),
                                keyword);
                break;
        }
        List<SimpleStore> stores = new ArrayList<>();
        for (StoreInfo info : infos) {
            stores.add(info.convert2Dto());
        }
        return stores;
    }

    public Boolean updateStores(List<Store> stores) {
        storeRepository.saveAll(stores);
        return true;
    }

    public Store selectStore(Integer id) {
        return storeRepository.findById(id).orElseThrow(() -> {
            throw new Error("상점 정보를 찾을 수 없습니다.");
        });
    }

    public StoreInfo selectStoreInfo(Integer id) {
        return storeInfoRepository.findById(id).orElseThrow(() -> {
            throw new Error("상점 정보를 찾을 수 없습니다.");
        });
    }

    public List<Store> selectStoreList(Boolean isAdmin) {
        if (isAdmin) {
            return storeRepository.findAll();
        } else {
            return storeRepository.findAllByStateEquals(StoreState.ACTIVE);
        }
    }

    public Store selectStoreByLoginId(String loginId) {
        Store store = storeRepository.findByLoginId(loginId).orElseThrow(() -> {
            throw new Error("스토어 정보를 찾을 수 없습니다.");
        });
        return store;
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

    public List<StoreInfo> selectScrapedStore(Integer userId) {
        List<StoreScrap> storeScraps = storeScrapRepository.findByUserId(userId);
        List<Integer> storeIds = new ArrayList<>();
        for (StoreScrap storeScrap : storeScraps) {
            storeIds.add(storeScrap.getStoreId());
        }
        List<StoreInfo> storeInfos = storeInfoRepository.findAllByStoreIdIn(storeIds);
        return storeInfos;
    }

    public void deleteScrapedStore(Integer userId, List<Integer> storeIds) {
        List<StoreScrap> storeScraps = new ArrayList<>();
        for (Integer storeId : storeIds) {
            storeScraps.add(StoreScrap.builder().storeId(storeId).userId(userId).build());
        }

        storeScrapRepository.deleteAll(storeScraps);
    }

    public void likeStore(Integer storeId, Integer userId) {
        storeScrapRepository.save(StoreScrap.builder().storeId(storeId).userId(userId).build());
    }

    public void unlikeStore(Integer storeId, Integer userId) {
        storeScrapRepository.deleteById(StoreScrapId.builder().storeId(storeId).userId(userId).build());
    }

    public Boolean checkLikeStore(Integer storeId, Integer userId) {
        return storeScrapRepository.existsByStoreIdAndUserId(storeId, userId);
    }
}
