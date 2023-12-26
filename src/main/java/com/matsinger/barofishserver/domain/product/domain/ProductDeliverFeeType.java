package com.matsinger.barofishserver.domain.product.domain;

public enum ProductDeliverFeeType {

    FREE, C_FIX, C_FREE_IF_OVER,
    FIX, FREE_IF_OVER;

    public static boolean canCombinedShipping(ProductDeliverFeeType deliverFeeType) {
        if (deliverFeeType.equals(FREE) ||
            deliverFeeType.equals(FIX) ||
            deliverFeeType.equals(FREE_IF_OVER)) {
            return true;
        }
        return false;
    }
}

