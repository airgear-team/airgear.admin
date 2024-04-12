package com.airgear.admin.service;

import com.airgear.admin.dto.UserCountByNameResponse;
import com.airgear.admin.dto.UserCountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;

public interface GoodsService {

    UserCountResponse getCountOfGoods();

    UserCountResponse getCountOfTopGoods();

    UserCountResponse getCountOfNewGoods(OffsetDateTime fromDate, OffsetDateTime toDate);

    UserCountByNameResponse getCountOfDeletedGoods(OffsetDateTime fromDate, OffsetDateTime toDate, String category);

    Page<UserCountByNameResponse> getCountOfGoodsByCategory(Pageable pageable);

    Page<UserCountByNameResponse> getCountOfNewGoodsByCategory(OffsetDateTime fromDate, OffsetDateTime toDate, Pageable pageable);
}
