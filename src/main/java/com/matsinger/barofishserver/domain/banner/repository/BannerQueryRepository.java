package com.matsinger.barofishserver.domain.banner.repository;

import com.matsinger.barofishserver.domain.banner.domain.BannerOrderBy;
import com.matsinger.barofishserver.domain.banner.domain.BannerType;
import com.matsinger.barofishserver.domain.banner.dto.BannerDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.matsinger.barofishserver.domain.banner.domain.QBanner.banner;
import static com.matsinger.barofishserver.domain.category.domain.QCategory.category;
import static com.matsinger.barofishserver.domain.data.curation.domain.QCuration.curation;
import static com.matsinger.barofishserver.domain.notice.domain.QNotice.notice;

@Repository
@RequiredArgsConstructor
public class BannerQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<BannerDto> selectBannersByAdmin(Pageable pageable,
                                                List<BannerType> types,
                                                BannerOrderBy orderBy,
                                                Sort.Direction sort) {

        return queryFactory
                .select(Projections.fields(
                        BannerDto.class,
                        banner.id.as("id"),
                        banner.state.as("state"),
                        banner.type.as("type"),
                        banner.image.as("image"),
                        banner.sortNo.as("sortNo"),
                        banner.link.as("link"),
                        curation.id.as("curationId"),
                        curation.title.as("curationName"),
                        banner.noticeId.as("noticeId"),
                        notice.title.as("noticeTitle"),
                        banner.categoryId.as("categoryId"),
                        category.name.as("categoryName")
                ))
                .from(banner)
                .leftJoin(curation).on(banner.curationId.eq(curation.id))
                .leftJoin(notice).on(banner.noticeId.eq(notice.id))
                .leftJoin(category).on(banner.categoryId.eq(category.id))
                .where(
                        includeBannerTypes(types)
                )
                .orderBy(
                       bannerOrderBy(orderBy, sort)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private OrderSpecifier[] bannerOrderBy(BannerOrderBy orderBy, Sort.Direction sort) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        Order order = null;
        if (sort.isAscending()) {
            order = Order.ASC;
        } else {
            order = Order.DESC;
        }
        switch (orderBy) {
            case id:
                orderSpecifiers.add(new OrderSpecifier(order, banner.id));
                break;
            case type:
                orderSpecifiers.add(new OrderSpecifier(order, banner.type));
                break;
            case curationId:
                orderSpecifiers.add(new OrderSpecifier(order, banner.curationId));
                break;
            case noticeId:
                orderSpecifiers.add(new OrderSpecifier(order, banner.noticeId));
                break;
            case categoryId:
                orderSpecifiers.add(new OrderSpecifier(order, banner.categoryId));
                break;
            case sortNo:
                orderSpecifiers.add(new OrderSpecifier(order, banner.sortNo));
                break;
            default:
                // 처리하지 않은 다른 경우에 대한 처리 (예: 기본 정렬 설정 등)
                break;
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    private Predicate includeBannerTypes(List<BannerType> types) {
        if (types == null || types.isEmpty()) {
            return null;
        }
        return banner.type.in(types);
    }
}
