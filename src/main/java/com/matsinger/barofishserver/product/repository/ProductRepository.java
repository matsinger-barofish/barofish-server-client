package com.matsinger.barofishserver.product.repository;

import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.domain.ProductState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    List<Product> findAllByIdIn(List<Integer> ids);

    List<Product> findAllByStateNot(ProductState state);

    List<Product> findByTitleContainsAndStateEquals(String title, ProductState state);

    List<Product> findByTitleContaining(String keyword);

    Optional<Product> findByTitle(String title);

    Optional<Product> findByTitleAndStoreId(String title, Integer storeId);

    Integer countAllByStoreId(Integer storeId);

    List<Product> findByStoreIdAndStateEquals(Integer storeId, ProductState state);

//    Page<Product> findAllByStateIn(List<ProductState> state, Pageable pageable, Specification<Product> spec);

//    Page<Product> findAllByStoreId(Integer storeId, Pageable pageable, Specification<Product> spec);


    @Query(value = "SELECT p.*\n" +
            "FROM product p\n" +
            "WHERE p.id IN (SELECT DISTINCT ci.product_id\n" +
            "               FROM compare_set\n" +
            "                        JOIN compare_item ci ON compare_set.id = ci.compare_set_id\n" +
            "               WHERE ci.product_id = :productId) AND p.id != :productId\n" +
            "ORDER BY RAND( )\n" +
            "LIMIT 2;", nativeQuery = true)
    List<Product> selectComparedProductList(Integer productId);

    @Query(value = "select p.id from product p where (:#{#ids==null ? null : #ids.size()} is null or p.id in (:ids))", nativeQuery = true)
    List<Integer> testQuery(@Param("ids") List<Integer> ids);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "LEFT OUTER JOIN product_like pl ON p.id = pl.product_id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n" +
            "order by COUNT (*) desc", nativeQuery = true, countQuery = "select count(*) from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "LEFT OUTER JOIN product_like pl ON p.id = pl.product_id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n")
    Page<Product> findWithPaginationSortByRecommend(Pageable pageable,
                                                    @Param("categoryIds") List<Integer> categoryIds,
                                                    @Param("filterFieldIds") List<Integer> filterFieldIds,
                                                    @Param("curationId") Integer curationId,
                                                    @Param("keyword") String keyword,
                                                    @Param("storeId") Integer storeId);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n" +
            "order by p.created_at desc", nativeQuery = true, countQuery = "select count(*) from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n")
    Page<Product> findWithPaginationSortByNewer(Pageable pageable,
                                                @Param("categoryIds") List<Integer> categoryIds,
                                                @Param("filterFieldIds") List<Integer> filterFieldIds,
                                                @Param("curationId") Integer curationId,
                                                @Param("keyword") String keyword,
                                                @Param("storeId") Integer storeId);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "JOIN option_item oi ON oi.id = p.represent_item_id\n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n" +
            "order by oi.discount_price asc", nativeQuery = true, countQuery = "select count(*) from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n")
    Page<Product> findWithPaginationSortByLowPrice(Pageable pageable,
                                                   @Param("categoryIds") List<Integer> categoryIds,
                                                   @Param("filterFieldIds") List<Integer> filterFieldIds,
                                                   @Param("curationId") Integer curationId,
                                                   @Param("keyword") String keyword,
                                                   @Param("storeId") Integer storeId);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "JOIN option_item oi ON oi.id = p.represent_item_id\n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n" +
            "order by oi.discount_price desc", nativeQuery = true, countQuery = "select count(*) from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n")
    Page<Product> findWithPaginationSortByHighPrice(Pageable pageable,
                                                    @Param("categoryIds") List<Integer> categoryIds,
                                                    @Param("filterFieldIds") List<Integer> filterFieldIds,
                                                    @Param("curationId") Integer curationId,
                                                    @Param("keyword") String keyword,
                                                    @Param("storeId") Integer storeId);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "LEFT OUTER JOIN product_like pl ON p.id = pl.product_id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n" +
            "order by COUNT(*) desc", nativeQuery = true, countQuery = "select count(*) from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "LEFT OUTER JOIN product_like pl ON p.id = pl.product_id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n")
    Page<Product> findWithPaginationSortByLike(Pageable pageable,
                                               @Param("categoryIds") List<Integer> categoryIds,
                                               @Param("filterFieldIds") List<Integer> filterFieldIds,
                                               @Param("curationId") Integer curationId,
                                               @Param("keyword") String keyword,
                                               @Param("storeId") Integer storeId);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "LEFT OUTER JOIN review r ON p.id = r.product_id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n" +
            "order by COUNT(*) desc", nativeQuery = true, countQuery = "select count(*) from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "LEFT OUTER JOIN review r ON p.id = r.product_id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n")
    Page<Product> findWithPaginationSortByReview(Pageable pageable,
                                                 @Param("categoryIds") List<Integer> categoryIds,
                                                 @Param("filterFieldIds") List<Integer> filterFieldIds,
                                                 @Param("curationId") Integer curationId,
                                                 @Param("keyword") String keyword,
                                                 @Param("storeId") Integer storeId);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "LEFT OUTER JOIN order_product_info o ON p.id = o.product_id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and (:storeId is null or p.store_id = :storeId) \n" +
            "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "group by p.id \n" +
            "order by COUNT( o.state not in (\'CANCELED\',\'WAIT_DEPOSIT\')) desc", nativeQuery = true, countQuery =
            "select count(*) from " +
                    "product " +
                    "p \n" +
                    "join category c ON p.category_id = c.id \n" +
                    "LEFT OUTER JOIN order_product_info o ON p.id = o.product_id \n" +
                    "where p.state = \'ACTIVE\' \n" +
                    "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
                    "WHERE cpm.curation_id=:curationId)) \n" +
                    "and (:storeId is null or p.store_id = :storeId) \n" +
                    "and (:keyword is null or p.title like concat('%', :keyword, '%') ) \n" +
                    "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
                    "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
                    "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
                    "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
                    "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
                    "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
                    "() } )) \n" +
                    "group by p.id \n")
    Page<Product> findWithPaginationSortByOrder(Pageable pageable,
                                                @Param("categoryIds") List<Integer> categoryIds,
                                                @Param("filterFieldIds") List<Integer> filterFieldIds,
                                                @Param("curationId") Integer curationId,
                                                @Param("keyword") String keyword,
                                                @Param("storeId") Integer storeId);

    @Query(value = "select p.* from product p " +
            "join category c ON p.category_id = c.id " +
            "where p.state = \'ACTIVE\' " +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "order by p.created_at desc", nativeQuery = true, countQuery = "select count(*) from product p " +
            "join category c ON p.category_id = c.id " +
            "where p.state = \'ACTIVE\' " +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n")
    Page<Product> findNewerWithPagination(Pageable pageable,
                                          @Param("categoryIds") List<Integer> categoryIds,
                                          @Param("filterFieldIds") List<Integer> filterFieldIds,
                                          @Param("curationId") Integer curationId);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "JOIN option_item oi ON p.represent_item_id = oi.id\n" +
            "where p.state = \'ACTIVE\'" +
            "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
            "WHERE cpm.curation_id=:curationId)) \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
            "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
            "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
            "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
            "() } )) \n" +
            "and oi.origin_price != 0\n" +
            " ORDER BY IF( oi.origin_price != 0, oi.discount_price / oi.origin_price, 100 )", nativeQuery = true, countQuery =
            "select count(*) from product p " +
                    "join category c ON p.category_id = c.id " +
                    "where p.state = \'ACTIVE\'" +
                    "and (:curationId is null or p.id in (select cpm.product_id from curation_product_map cpm " +
                    "WHERE cpm.curation_id=:curationId)) \n" +
                    "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
                    "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
                    "and (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or p.id in (select p1.id from " +
                    "product p1 inner join product_search_filter_map ps on ps.product_id = p1.id\n" +
                    "where (:#{#filterFieldIds==null ? null : #filterFieldIds.size() } is null or  ps.field_id in " +
                    "(:filterFieldIds) ) GROUP BY p1.id HAVING COUNT(*) = :#{#filterFieldIds==null ? 0 : #filterFieldIds.size" +
                    "() } )) \n" +
                    "and oi.origin_price != 0\n")
    Page<Product> findDiscountWithPagination(Pageable pageable,
                                             @Param("categoryIds") List<Integer> categoryIds,
                                             @Param("filterFieldIds") List<Integer> filterFieldIds,
                                             @Param("curationId") Integer curationId);

    @Query(value = "insert into product_like (product_id, user_id) value (:productId, :userId)", nativeQuery = true)
    void likeProduct(Integer productId, Integer userId);

    @Query(value = "delete from product_like where product_id=:productId and user_id=:userId", nativeQuery = true)
    void unlikeProduct(Integer productId, Integer userId);

    @Query(value = "select EXISTS(select * from product_like WHERE product_id=:productId and user_id=:userId) as " +
            "isExist", nativeQuery = true)
    Integer checkLikeProduct(Integer productId, Integer userId);

    @Query(value = "select * from product " +
            "where instr(title, :keyword) > 0 " +
            "and state = \'ACTIVE\'", nativeQuery = true)
    List<Product> searchProductList(String keyword, Pageable pageable);

    @Query(value = "SELECT *\n" +
            "FROM product pp\n" +
            "WHERE pp.state=\'ACTIVE\' AND pp.id IN\n" +
            "      (SELECT i.product_id AS productId\n" +
            "       FROM order_product_info i\n" +
            "       WHERE i.order_id IN\n" +
            "             (SELECT o1.id\n" +
            "              FROM orders o1\n" +
            "                       JOIN order_product_info opi ON o1.id = opi.order_id\n" +
            "                       JOIN product p ON p.id = opi.product_id\n" +
            "              WHERE p.id IN (:productIds)\n" +
            "              GROUP BY o1.id)\n" +
            "         AND i.product_id NOT IN (:productIds)\n" +
            "       GROUP BY i.product_id)\n" +
            "LIMIT 10", nativeQuery = true)
    List<Product> selectProductOtherCustomerBuy(@Param("productIds") List<Integer> productIds);

}