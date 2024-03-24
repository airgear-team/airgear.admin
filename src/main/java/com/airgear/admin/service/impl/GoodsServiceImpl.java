package com.airgear.admin.service.impl;

import com.airgear.admin.dto.CountByNameDto;
import com.airgear.admin.dto.CountDto;
import com.airgear.admin.model.Category;
import com.airgear.admin.repository.GoodsRepository;
import com.airgear.admin.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service(value = "goodsService")
public class GoodsServiceImpl implements GoodsService {

    private final GoodsRepository goodsRepository;

    @Autowired
    public GoodsServiceImpl(GoodsRepository goodsRepository) {
        this.goodsRepository = goodsRepository;
    }

    @Override
    public CountDto getCountOfGoods() {
        return new CountDto(goodsRepository.count());
    }

    @Override
    public CountDto getCountOfTopGoods() {
        return new CountDto(goodsRepository.countTopGoods());
    }

    @Override
    public CountDto getCountOfNewGoods(OffsetDateTime fromDate, OffsetDateTime toDate) {
        return new CountDto(goodsRepository.countByCreatedAtBetween(fromDate, toDate));
    }

    @Override
    public CountByNameDto getCountOfDeletedGoods(OffsetDateTime fromDate, OffsetDateTime toDate, String category) {

        return category == null ?
                new CountByNameDto("",goodsRepository.countByDeletedAtBetween(fromDate, toDate)):
                new CountByNameDto(category,goodsRepository.countByDeletedAtBetweenAndCategoryName(fromDate, toDate, category));

    }

    @Override
    public Page<CountByNameDto> getCountOfGoodsByCategory(Pageable pageable) {
        Page<Object> page =goodsRepository.countGoodsByCategory(pageable);
        return page == null ? null : page.map(x -> (Object[]) x).map(x -> new CountByNameDto(((Category) x[0]).getName(), (Long) x[1]));
    }

    @Override
    public Page<CountByNameDto> getCountOfNewGoodsByCategory(OffsetDateTime fromDate, OffsetDateTime toDate, Pageable pageable) {
        Page<Object> page =goodsRepository.countNewGoodsByCategory(fromDate,toDate, pageable);
        return page == null ? null : page.map(x -> (Object[]) x).map(x -> new CountByNameDto(((Category) x[0]).getName(), (Long) x[1]));
    }
}
