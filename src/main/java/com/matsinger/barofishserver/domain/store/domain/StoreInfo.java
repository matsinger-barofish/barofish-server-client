package com.matsinger.barofishserver.domain.store.domain;

import com.matsinger.barofishserver.domain.basketProduct.dto.BasketStoreDto;
import com.matsinger.barofishserver.domain.store.dto.SimpleStore;
import com.matsinger.barofishserver.domain.store.dto.StoreAdditionalDto;
import com.matsinger.barofishserver.domain.store.dto.StoreDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "store_info", schema = "barofish_dev", catalog = "")
public class StoreInfo implements ConditionalObject{
    @Id
    @Column(name = "store_id", nullable = false)
    private int storeId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "store_id", updatable = false, insertable = false)
    private Store store;

    public void setStore(Store store) {
        this.store = store;
        store.setStoreInfo(this);
    }

    @Basic
    @Column(name = "background_image", nullable = false, length = -1)
    private String backgroundImage;
    @Basic
    @Column(name = "profile_image", nullable = false, length = -1)
    private String profileImage;
    @Basic
    @Column(name = "is_reliable", nullable = false)
    private Boolean isReliable;
    @Basic
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Basic
    @Column(name = "location", nullable = false, length = 50)
    private String location;
    @Basic
    @Column(name = "keyword", nullable = false, length = -1)
    private String keyword;

    @Basic
    @Column(name = "visit_note", nullable = true, columnDefinition = "TEXT")
    private String visitNote;


    @Basic
    @Column(name = "one_line_description", nullable = false)
    private String oneLineDescription;

    @Basic
    @Column(name = "refund_deliver_fee", nullable = true)
    private Integer refundDeliverFee;

    @Column(name = "is_conditional", nullable = false)
    private Boolean isConditional;
    @Basic
    @Column(name = "min_store_price", nullable = true)
    private Integer minStorePrice;
    @Column(name = "delivery_fee")
    private Integer deliveryFee;
    @Basic
    @Column(name = "settlement_rate", nullable = true)
    Float settlementRate;
    @Basic
    @Column(name = "bank_name", nullable = true)
    String bankName;
    @Basic
    @Column(name = "bank_holder", nullable = true)
    String bankHolder;
    @Basic
    @Column(name = "bank_account", nullable = true)
    String bankAccount;
    @Basic
    @Column(name = "representative_name", nullable = true)
    String representativeName;
    @Basic
    @Column(name = "company_id", nullable = true)
    String companyId;
    @Basic
    @Column(name = "business_type", nullable = true)
    String businessType;
    @Basic
    @Column(name = "mos_registration_number", nullable = true)
    String mosRegistrationNumber;
    @Basic
    @Column(name = "business_address", nullable = true)
    String businessAddress;
    @Basic
    @Column(name = "postal_code", nullable = true)
    String postalCode;
    @Basic
    @Column(name = "lot_number_address", nullable = true)
    String lotNumberAddress;
    @Basic
    @Column(name = "street_name_address", nullable = true)
    String streetNameAddress;
    @Basic
    @Column(name = "address_detail", nullable = true)
    String addressDetail;
    @Basic
    @Column(name = "tel", nullable = true)
    String tel;
    @Basic
    @Column(name = "email", nullable = true)
    String email;
    @Basic
    @Column(name = "fax_number", nullable = true)
    String faxNumber;
    @Basic
    @Column(name = "mos_registration", nullable = true)
    String mosRegistration;
    @Basic
    @Column(name = "business_registration", nullable = true)
    String businessRegistration;

    @Basic
    @Column(name = "bank_account_copy", nullable = true)
    String bankAccountCopy;

    @Basic
    @Column(name = "deliver_company", nullable = true)
    String deliverCompany;


    public String getbackgroundImage() {
        return backgroundImage;
    }

    public void setbackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public SimpleStore convert2Dto() {
        return SimpleStore.builder().storeId(this.store.getId()).backgroundImage(backgroundImage).profileImage(
                profileImage).name(name).location(location).keyword(keyword.split(",")).visitNote(this.getVisitNote()).refundDeliverFee(
                this.refundDeliverFee).oneLineDescription(this.oneLineDescription).build();
    }

    public StoreAdditionalDto toAdditionalDto(Boolean isUser) {
        if (isUser) {
            return null;
        }

        return StoreAdditionalDto.builder()
                .settlementRate(settlementRate)
                .bankName(bankName)
                .bankHolder(bankHolder)
                .bankAccount(bankAccount)
                .representativeName(representativeName)
                .companyId(companyId)
                .businessType(businessType)
                .mosRegistrationNumber(mosRegistrationNumber)
                .businessAddress(businessAddress)
                .postalCode(postalCode)
                .lotNumberAddress(lotNumberAddress)
                .streetNameAddress(streetNameAddress)
                .addressDetail(addressDetail)
                .tel(tel)
                .email(email)
                .faxNumber(faxNumber)
                .mosRegistration(mosRegistration)
                .businessRegistration(businessRegistration)
                .bankAccountCopy(bankAccountCopy)
                .isConditional(isConditional)
                .minOrderPrice(minStorePrice)
                .deliveryFee(deliveryFee)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreInfo that = (StoreInfo) o;
        return
//                storeId == that.storeId &&
                Objects.equals(backgroundImage, that.backgroundImage) &&
                        Objects.equals(profileImage, that.profileImage) &&
                        Objects.equals(name, that.name) &&
                        Objects.equals(location, that.location) &&
                        Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backgroundImage, profileImage, name, location, keyword);
    }

    public StoreDto toStoreDto(Store store, Boolean isUser) {
        return StoreDto.builder()
                .id(store.getId())
                .state(store.getState())
                .loginId(store.getLoginId())
                .joinAt(store.getJoinAt())
                .backgroundImage(backgroundImage)
                .profileImage(profileImage)
                .name(name)
                .isReliable(isReliable)
                .location(location)
                .visitNote(visitNote)
                .keyword(keyword.split(","))
                .oneLineDescription(oneLineDescription)
                .refundDeliverFee(refundDeliverFee)
                .additionalData(toAdditionalDto(isUser))
                .deliverCompany(deliverCompany)
                .isConditional(isConditional)
                .minOrderPrice(minStorePrice)
                .deliveryFee(deliveryFee)
                .build();
    }

    public boolean isConditional() {
        return isConditional;
    }

    public BasketStoreDto toBasketStoreDto() {
        return BasketStoreDto.builder()
                .storeId(storeId)
                .name(name)
                .backgroundImage(backgroundImage)
                .profileImage(profileImage)
                .isConditional(isConditional)
                .minStorePrice(minStorePrice)
                .deliveryFee(deliveryFee)
                .build();
    }

    @Override
    public Integer getId() {
        return this.storeId;
    }

    @Override
    public Boolean meetConditions(int totalStoreProductPrice) {
        return totalStoreProductPrice >= this.minStorePrice;
    }
}

