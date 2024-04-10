package com.airgear.admin.service;

import com.airgear.admin.dto.CountByNameDto;
import com.airgear.admin.dto.CountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;

public interface GoodsService {

    CountDto getCountOfGoods();
    CountDto getCountOfTopGoods();
    CountDto getCountOfNewGoods(OffsetDateTime fromDate, OffsetDateTime toDate);


    CountByNameDto getCountOfDeletedGoods(OffsetDateTime fromDate, OffsetDateTime toDate, String category);
    Page<CountByNameDto> getCountOfGoodsByCategory(Pageable pageable);
    Page<CountByNameDto> getCountOfNewGoodsByCategory(OffsetDateTime fromDate, OffsetDateTime toDate, Pageable pageable);










}
