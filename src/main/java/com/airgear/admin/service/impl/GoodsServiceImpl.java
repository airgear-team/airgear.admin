package com.airgear.admin.service.impl;

import com.airgear.admin.dto.UserCountByNameResponse;
import com.airgear.admin.dto.UserCountResponse;
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
    public UserCountResponse getCountOfGoods() {
        return new UserCountResponse(goodsRepository.count());
    }

    @Override
    public UserCountResponse getCountOfTopGoods() {
        return new UserCountResponse(goodsRepository.countTopGoods());
    }

    @Override
    public UserCountResponse getCountOfNewGoods(OffsetDateTime fromDate, OffsetDateTime toDate) {
        return new UserCountResponse(goodsRepository.countByCreatedAtBetween(fromDate, toDate));
    }

    @Override
    public UserCountByNameResponse getCountOfDeletedGoods(OffsetDateTime fromDate, OffsetDateTime toDate, String category) {

        return category == null ?
                new UserCountByNameResponse("",goodsRepository.countByDeletedAtBetween(fromDate, toDate)):
                new UserCountByNameResponse(category,goodsRepository.countByDeletedAtBetweenAndCategoryName(fromDate, toDate, category));

    }

    @Override
    public Page<UserCountByNameResponse> getCountOfGoodsByCategory(Pageable pageable) {
        Page<Object> page =goodsRepository.countGoodsByCategory(pageable);
        return page == null ? null : page.map(x -> (Object[]) x).map(x -> new UserCountByNameResponse(((Category) x[0]).getName(), (Long) x[1]));
    }

    @Override
    public Page<UserCountByNameResponse> getCountOfNewGoodsByCategory(OffsetDateTime fromDate, OffsetDateTime toDate, Pageable pageable) {
        Page<Object> page =goodsRepository.countNewGoodsByCategory(fromDate,toDate, pageable);
        return page == null ? null : page.map(x -> (Object[]) x).map(x -> new UserCountByNameResponse(((Category) x[0]).getName(), (Long) x[1]));
    }
}
