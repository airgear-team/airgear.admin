package com.airgear.admin.repository;

import com.airgear.model.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface GoodsRepository extends JpaRepository<Goods, Long> {

    @Query("SELECT COUNT(t) FROM TopGoodsPlacement t WHERE t.startAt <= CURRENT_TIMESTAMP AND t.endAt >= CURRENT_TIMESTAMP")
    Long countTopGoods();

    Long countByCreatedAtBetween(OffsetDateTime fromDate, OffsetDateTime toDate);

    Long countByDeletedAtBetween(OffsetDateTime fromDate, OffsetDateTime toDate);

    Long countByDeletedAtBetweenAndCategoryName(OffsetDateTime fromDate, OffsetDateTime toDate, String categoryName);

    @Query("SELECT category, count(goods.description) FROM Goods goods group by goods.category")
    Page<Object> countGoodsByCategory(Pageable pageable);

    @Query("SELECT category, count(goods.description) as result FROM Goods goods WHERE goods.createdAt >= :fromDate AND goods.createdAt <= :toDate group by goods.category")
    Page<Object> countNewGoodsByCategory(@Param("fromDate") OffsetDateTime fromDate, @Param("toDate") OffsetDateTime toDate, Pageable pageable);
}
