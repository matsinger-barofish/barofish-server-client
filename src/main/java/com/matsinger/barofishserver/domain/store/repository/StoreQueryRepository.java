package com.matsinger.barofishserver.domain.store.repository;

import com.matsinger.barofishserver.domain.store.dto.StoreExcelInquiryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.matsinger.barofishserver.domain.store.domain.QStore.store;
import static com.matsinger.barofishserver.domain.store.domain.QStoreInfo.storeInfo;

@Repository
@RequiredArgsConstructor
public class StoreQueryRepository {

    private final JPAQueryFactory queryFactory;


    public List<StoreExcelInquiryDto> getExcelDataByStoreIds(List<Integer> storeIds) {
        return queryFactory.select(Projections.fields(
                StoreExcelInquiryDto.class,
                storeInfo.name.as("storeName"),
                store.loginId.as("loginId"),
                store.state.as("state"),
                storeInfo.location.as("location"),
                storeInfo.keyword.as("keyowrd"),
                storeInfo.settlementRate.as("settlementRete"),
                storeInfo.bankName.as("bankName"),
                storeInfo.bankHolder.as("bankHolder"),
                storeInfo.bankAccount.as("bankAccount"),
                storeInfo.representativeName.as("representativeName"),
                storeInfo.companyId.as("companyId"),
                storeInfo.businessType.as("businessType"),
                storeInfo.mosRegistrationNumber.as("mosRegistrationNumber"),
                storeInfo.businessAddress.as("businessAddress"),
                storeInfo.postalCode.as("postalCode"),
                storeInfo.lotNumberAddress.as("lotNumberAddress"),
                storeInfo.streetNameAddress.as("streetNameAddress"),
                storeInfo.addressDetail.as("addressDetail"),
                storeInfo.tel.as("phoneNumber"),
                storeInfo.email.as("email"),
                storeInfo.faxNumber.as("faxNumber")
                ))
                .from(store)
                .leftJoin(storeInfo).on(store.id.eq(storeInfo.storeId))
                .where(
                        containsStoreIds(storeIds)
                )
                .orderBy(store.id.asc())
                .fetch();
    }

    private BooleanExpression containsStoreIds(List<Integer> storeIds) {
        if (storeIds == null || storeIds.isEmpty()) {
            return null;
        }
        return store.id.in(storeIds);
    }
}
