package com.airgear.admin.service;

import com.airgear.admin.dto.GoodsDto;
import com.airgear.admin.dto.UserCountByNameResponse;
import com.airgear.admin.dto.UserCountResponse;
import com.airgear.model.Goods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public interface GoodsService {
    Page<GoodsDto> getGoods(Specification<Goods> spec, Pageable pageable);

    UserCountResponse getCountOfGoods();

    UserCountResponse getCountOfTopGoods();

    UserCountResponse getCountOfNewGoods(OffsetDateTime fromDate, OffsetDateTime toDate);

    UserCountByNameResponse getCountOfDeletedGoods(OffsetDateTime fromDate, OffsetDateTime toDate, String category);

    Page<UserCountByNameResponse> getCountOfGoodsByCategory(Pageable pageable);

    Page<UserCountByNameResponse> getCountOfNewGoodsByCategory(OffsetDateTime fromDate, OffsetDateTime toDate, Pageable pageable);
}
