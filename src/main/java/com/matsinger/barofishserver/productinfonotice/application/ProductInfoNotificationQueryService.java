package com.matsinger.barofishserver.productinfonotice.application;

import com.matsinger.barofishserver.productinfonotice.domain.ProductInfoNoticeManager;
import com.matsinger.barofishserver.productinfonotice.domain.ProductInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductInfoNotificationQueryService {

    public ProductInformation getProductInfoNotificationKeys(String itemCode) {
        return ProductInfoNoticeManager.getProductInformationForm(itemCode);
    }
}
