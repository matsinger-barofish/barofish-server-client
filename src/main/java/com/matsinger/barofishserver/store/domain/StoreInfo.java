package com.matsinger.barofishserver.store.domain;

import com.matsinger.barofishserver.store.dto.SimpleStore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "store_info", schema = "barofish_dev", catalog = "")
public class StoreInfo {
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
    @Column(name = "backgroud_image", nullable = false, length = -1)
    private String backgroudImage;
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
    @Enumerated(EnumType.STRING)
    @ColumnDefault("FREE")
    @Column(name = "deliver_fee_type", nullable = false)
    private StoreDeliverFeeType deliverFeeType;

    @Basic
    @Column(name = "one_line_description", nullable = false)
    private String oneLineDescription;

    @Basic
    @ColumnDefault("0")
    @Column(name = "deliver_fee", nullable = false)
    private Integer deliverFee;
    @Basic
    @Column(name = "refund_deliver_fee", nullable = true)
    private Integer refundDeliverFee;
    @Basic
    @Column(name = "min_order_price", nullable = true)
    private Integer minOrderPrice;
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


    public String getBackgroudImage() {
        return backgroudImage;
    }

    public void setBackgroudImage(String backgroudImage) {
        this.backgroudImage = backgroudImage;
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
        return SimpleStore.builder().storeId(this.store.getId()).backgroundImage(backgroudImage).profileImage(
                profileImage).name(name).location(location).keyword(keyword.split(",")).visitNote(this.getVisitNote()).deliverFee(
                this.deliverFee).minOrderPrice(this.minOrderPrice).refundDeliverFee(this.refundDeliverFee).oneLineDescription(
                this.oneLineDescription).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreInfo that = (StoreInfo) o;
        return
//                storeId == that.storeId &&
                Objects.equals(backgroudImage, that.backgroudImage) &&
                        Objects.equals(profileImage, that.profileImage) &&
                        Objects.equals(name, that.name) &&
                        Objects.equals(location, that.location) &&
                        Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backgroudImage, profileImage, name, location, keyword);
    }
}

