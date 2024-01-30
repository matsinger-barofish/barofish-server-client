package com.matsinger.barofishserver.domain.basketProduct.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class BasketProductInfos {

    private List<BasketProductInfo> basketProductInfos;

    public BasketProductInfos(List<BasketProductInfo> basketProductInfos) {
        this.basketProductInfos = basketProductInfos;
    }

    public BasketProductInfo getSameOptionItem(Integer optionItemId) {
        for (BasketProductInfo basketProductInfo : basketProductInfos) {
            if (basketProductInfo.containsSameOptionItem(optionItemId)) {
                return basketProductInfo;
            }
        }
        return null;
    }

    public void addQuantity(Integer optionItemId, Integer amount) {
        for (BasketProductInfo basketProductInfo : basketProductInfos) {
            basketProductInfo.addQuantity(optionItemId, amount);
        }
    }

    public BasketProductInfo getOptionItem(Integer optionItemId) {
        for (BasketProductInfo basketProductInfo : basketProductInfos) {
            if (basketProductInfo.containsSameOptionItem(optionItemId)) {
                return basketProductInfo;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return this.basketProductInfos.isEmpty();
    }

    public boolean containsNecessaryOptions() {
        for (BasketProductInfo basketProductInfo : basketProductInfos) {
            if (basketProductInfo.isNeeded()) {
                return true;
            }
        }
        return false;
    }
}
