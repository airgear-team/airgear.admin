package com.airgear.admin.repository;

import com.airgear.admin.dto.GoodsDto;
import com.airgear.model.Goods;
import com.airgear.model.Location;
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

    @Query("SELECT new com.airgear.admin.dto.GoodsDto(" +
            "g.id, " +
            "g.name, " +
            "g.description, " +
            "g.price.priceAmount, " +
            "g.weekendsPrice.weekendsPriceAmount, " +
            "g.location.settlement, " +
            "g.deposit.amount, " +
            "g.user.name, " +
            "g.verificationStatus, " +
            "g.status, " +
            "g.createdAt, " +
            "SIZE(g.goodsViews), " +
            "g.goodsCondition) " +
            "FROM Goods g")
    Page<GoodsDto> findAllGoods(Pageable pageable);
}
