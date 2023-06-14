package com.matsinger.barofishserver.product;

import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.object.ProductState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    public List<Product> findByTitleContainsAndStateEquals(String title, ProductState state);

    public List<Product> findByTitleContaining(String keyword);

    Optional<Product> findByTitle(String title);

    public List<Product> findByStoreIdAndStateEquals(Integer storeId, ProductState state);

    List<Product> findALlByStateNot(ProductState state);

    List<Product> findAllByStoreIdAndStateNot(Integer storeId, ProductState state);


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
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#locationIds==null ? null : #locationIds.size() } is null or p.location_id in (:locationIds)) \n" +
            "and (:#{#typeIds==null ? null : #typeIds.size() } is null or p.type_id in (:typeIds)) \n" +
            "and (:#{#processIds==null ? null : #processIds.size() } is null or p.process_id in (:processIds)) \n" +
            "and (:#{#usageIds==null ? null : #usageIds.size() } is null or p.usage_id in (:usageIds)) \n" +
            "and (:#{#storageIds==null ? null : #storageIds.size() } is null or p.storage_id in (:storageIds)) \n" +
            "group by p.id \n" +
            "order by COUNT (*) desc", nativeQuery = true)
    List<Product> findWithPaginationSortByRecommend(Pageable pageable,
                                                    @Param("categoryIds") List<Integer> categoryIds,
                                                    @Param("typeIds") List<Integer> typeIds,
                                                    @Param("locationIds") List<Integer> locationIds,
                                                    @Param("processIds") List<Integer> processIds,
                                                    @Param("usageIds") List<Integer> usageIds,
                                                    @Param("storageIds") List<Integer> storageIds);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#locationIds==null ? null : #locationIds.size() } is null or p.location_id in (:locationIds)) \n" +
            "and (:#{#typeIds==null ? null : #typeIds.size() } is null or p.type_id in (:typeIds)) \n" +
            "and (:#{#processIds==null ? null : #processIds.size() } is null or p.process_id in (:processIds)) \n" +
            "and (:#{#usageIds==null ? null : #usageIds.size() } is null or p.usage_id in (:usageIds)) \n" +
            "and (:#{#storageIds==null ? null : #storageIds.size() } is null or p.storage_id in (:storageIds)) \n" +
            "group by p.id \n" +
            "order by p.created_at desc", nativeQuery = true)
    List<Product> findWithPaginationSortByNewer(Pageable pageable,
                                                @Param("categoryIds") List<Integer> categoryIds,
                                                @Param("typeIds") List<Integer> typeIds,
                                                @Param("locationIds") List<Integer> locationIds,
                                                @Param("processIds") List<Integer> processIds,
                                                @Param("usageIds") List<Integer> usageIds,
                                                @Param("storageIds") List<Integer> storageIds);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#locationIds==null ? null : #locationIds.size() } is null or p.location_id in (:locationIds)) \n" +
            "and (:#{#typeIds==null ? null : #typeIds.size() } is null or p.type_id in (:typeIds)) \n" +
            "and (:#{#processIds==null ? null : #processIds.size() } is null or p.process_id in (:processIds)) \n" +
            "and (:#{#usageIds==null ? null : #usageIds.size() } is null or p.usage_id in (:usageIds)) \n" +
            "and (:#{#storageIds==null ? null : #storageIds.size() } is null or p.storage_id in (:storageIds)) \n" +
            "group by p.id \n" +
            "order by p.origin_price asc", nativeQuery = true)
    List<Product> findWithPaginationSortByLowPrice(Pageable pageable,
                                                   @Param("categoryIds") List<Integer> categoryIds,
                                                   @Param("typeIds") List<Integer> typeIds,
                                                   @Param("locationIds") List<Integer> locationIds,
                                                   @Param("processIds") List<Integer> processIds,
                                                   @Param("usageIds") List<Integer> usageIds,
                                                   @Param("storageIds") List<Integer> storageIds);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#locationIds==null ? null : #locationIds.size() } is null or p.location_id in (:locationIds)) \n" +
            "and (:#{#typeIds==null ? null : #typeIds.size() } is null or p.type_id in (:typeIds)) \n" +
            "and (:#{#processIds==null ? null : #processIds.size() } is null or p.process_id in (:processIds)) \n" +
            "and (:#{#usageIds==null ? null : #usageIds.size() } is null or p.usage_id in (:usageIds)) \n" +
            "and (:#{#storageIds==null ? null : #storageIds.size() } is null or p.storage_id in (:storageIds)) \n" +
            "group by p.id \n" +
            "order by p.origin_price desc", nativeQuery = true)
    List<Product> findWithPaginationSortByHighPrice(Pageable pageable,
                                                    @Param("categoryIds") List<Integer> categoryIds,
                                                    @Param("typeIds") List<Integer> typeIds,
                                                    @Param("locationIds") List<Integer> locationIds,
                                                    @Param("processIds") List<Integer> processIds,
                                                    @Param("usageIds") List<Integer> usageIds,
                                                    @Param("storageIds") List<Integer> storageIds);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "LEFT OUTER JOIN product_like pl ON p.id = pl.product_id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#locationIds==null ? null : #locationIds.size() } is null or p.location_id in (:locationIds)) \n" +
            "and (:#{#typeIds==null ? null : #typeIds.size() } is null or p.type_id in (:typeIds)) \n" +
            "and (:#{#processIds==null ? null : #processIds.size() } is null or p.process_id in (:processIds)) \n" +
            "and (:#{#usageIds==null ? null : #usageIds.size() } is null or p.usage_id in (:usageIds)) \n" +
            "and (:#{#storageIds==null ? null : #storageIds.size() } is null or p.storage_id in (:storageIds)) \n" +
            "group by p.id \n" +
            "order by COUNT(*) desc", nativeQuery = true)
    List<Product> findWithPaginationSortByLike(Pageable pageable,
                                               @Param("categoryIds") List<Integer> categoryIds,
                                               @Param("typeIds") List<Integer> typeIds,
                                               @Param("locationIds") List<Integer> locationIds,
                                               @Param("processIds") List<Integer> processIds,
                                               @Param("usageIds") List<Integer> usageIds,
                                               @Param("storageIds") List<Integer> storageIds);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "LEFT OUTER JOIN review r ON p.id = r.product_id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#locationIds==null ? null : #locationIds.size() } is null or p.location_id in (:locationIds)) \n" +
            "and (:#{#typeIds==null ? null : #typeIds.size() } is null or p.type_id in (:typeIds)) \n" +
            "and (:#{#processIds==null ? null : #processIds.size() } is null or p.process_id in (:processIds)) \n" +
            "and (:#{#usageIds==null ? null : #usageIds.size() } is null or p.usage_id in (:usageIds)) \n" +
            "and (:#{#storageIds==null ? null : #storageIds.size() } is null or p.storage_id in (:storageIds)) \n" +
            "group by p.id \n" +
            "order by COUNT(*) desc", nativeQuery = true)
    List<Product> findWithPaginationSortByReview(Pageable pageable,
                                                 @Param("categoryIds") List<Integer> categoryIds,
                                                 @Param("typeIds") List<Integer> typeIds,
                                                 @Param("locationIds") List<Integer> locationIds,
                                                 @Param("processIds") List<Integer> processIds,
                                                 @Param("usageIds") List<Integer> usageIds,
                                                 @Param("storageIds") List<Integer> storageIds);

    @Query(value = "select p.* from product p \n" +
            "join category c ON p.category_id = c.id \n" +
            "LEFT OUTER JOIN order_product_info o ON p.id = o.product_id \n" +
            "where p.state = \'ACTIVE\' \n" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#locationIds==null ? null : #locationIds.size() } is null or p.location_id in (:locationIds)) \n" +
            "and (:#{#typeIds==null ? null : #typeIds.size() } is null or p.type_id in (:typeIds)) \n" +
            "and (:#{#processIds==null ? null : #processIds.size() } is null or p.process_id in (:processIds)) \n" +
            "and (:#{#usageIds==null ? null : #usageIds.size() } is null or p.usage_id in (:usageIds)) \n" +
            "and (:#{#storageIds==null ? null : #storageIds.size() } is null or p.storage_id in (:storageIds)) \n" +
            "group by p.id \n" +
            "order by COUNT(*) desc", nativeQuery = true)
    List<Product> findWithPaginationSortByOrder(Pageable pageable,
                                                @Param("categoryIds") List<Integer> categoryIds,
                                                @Param("typeIds") List<Integer> typeIds,
                                                @Param("locationIds") List<Integer> locationIds,
                                                @Param("processIds") List<Integer> processIds,
                                                @Param("usageIds") List<Integer> usageIds,
                                                @Param("storageIds") List<Integer> storageIds);

    @Query(value = "select p.* from product p " +
            "join category c ON p.category_id = c.id " +
            "where p.state = \'ACTIVE\' " +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#locationIds==null ? null : #locationIds.size() } is null or p.location_id in (:locationIds)) " +
            "and (:#{#typeIds==null ? null : #typeIds.size() } is null or p.type_id in (:typeIds)) " +
            "and (:#{#processIds==null ? null : #processIds.size() } is null or p.process_id in (:processIds)) " +
            "and (:#{#usageIds==null ? null : #usageIds.size() } is null or p.usage_id in (:usageIds)) " +
            "and (:#{#storageIds==null ? null : #storageIds.size() } is null or p.storage_id in (:storageIds)) " +
            "order by p.created_at desc", nativeQuery = true)
    List<Product> findNewerWithPagination(Pageable pageable,
                                          @Param("categoryIds") List<Integer> categoryIds,
                                          @Param("typeIds") List<Integer> typeIds,
                                          @Param("locationIds") List<Integer> locationIds,
                                          @Param("processIds") List<Integer> processIds,
                                          @Param("usageIds") List<Integer> usageIds,
                                          @Param("storageIds") List<Integer> storageIds);

    @Query(value = "select p.* from product p " +
            "join category c ON p.category_id = c.id " +
            "where p.state = \'ACTIVE\'" +
            "and ( (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.parent_category_id in (:categoryIds)) \n" +
            "or (:#{#categoryIds==null ? null : #categoryIds.size()} is null or c.id in (:categoryIds)) )\n" +
            "and (:#{#locationIds==null ? null : #locationIds.size() } is null or p.location_id in (:locationIds)) " +
            "and (:#{#typeIds==null ? null : #typeIds.size() } is null or p.type_id in (:typeIds)) " +
            "and (:#{#processIds==null ? null : #processIds.size() } is null or p.process_id in (:processIds)) " +
            "and (:#{#usageIds==null ? null : #usageIds.size() } is null or p.usage_id in (:usageIds)) " +
            "and (:#{#storageIds==null ? null : #storageIds.size() } is null or p.storage_id in (:storageIds)) " +
            " order by p.discount_rate desc", nativeQuery = true)
    List<Product> findDiscountWithPagination(Pageable pageable,
                                             @Param("categoryIds") List<Integer> categoryIds,
                                             @Param("typeIds") List<Integer> typeIds,
                                             @Param("locationIds") List<Integer> locationIds,
                                             @Param("processIds") List<Integer> processIds,
                                             @Param("usageIds") List<Integer> usageIds,
                                             @Param("storageIds") List<Integer> storageIds);

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
            "WHERE pp.id IN\n" +
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