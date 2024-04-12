package com.airgear.admin.service.impl;

import com.airgear.admin.dto.UserCountByNameResponse;
import com.airgear.admin.dto.UserCountResponse;
import com.airgear.admin.model.Category;
import com.airgear.admin.repository.GoodsRepository;
import com.airgear.admin.service.GoodsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    private final GoodsRepository goodsRepository;

    public GoodsServiceImpl(GoodsRepository goodsRepository) {
        this.goodsRepository = goodsRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserCountResponse getCountOfGoods() {
        return UserCountResponse.fromCount(goodsRepository.count());
    }

    @Override
    @Transactional(readOnly = true)
    public UserCountResponse getCountOfTopGoods() {
        return UserCountResponse.fromCount(goodsRepository.countTopGoods());
    }

    @Override
    @Transactional(readOnly = true)
    public UserCountResponse getCountOfNewGoods(OffsetDateTime fromDate, OffsetDateTime toDate) {
        return UserCountResponse.fromCount(goodsRepository.countByCreatedAtBetween(fromDate, toDate));
    }

    @Override
    @Transactional(readOnly = true)
    public UserCountByNameResponse getCountOfDeletedGoods(OffsetDateTime fromDate, OffsetDateTime toDate, String category) {

        return category == null ?
                new UserCountByNameResponse("", goodsRepository.countByDeletedAtBetween(fromDate, toDate)) :
                new UserCountByNameResponse(category, goodsRepository.countByDeletedAtBetweenAndCategoryName(fromDate, toDate, category));

    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserCountByNameResponse> getCountOfGoodsByCategory(Pageable pageable) {
        Page<Object> page = goodsRepository.countGoodsByCategory(pageable);
        return page == null ? null : page.map(x -> (Object[]) x).map(x -> new UserCountByNameResponse(((Category) x[0]).getName(), (Long) x[1]));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserCountByNameResponse> getCountOfNewGoodsByCategory(OffsetDateTime fromDate, OffsetDateTime toDate, Pageable pageable) {
        Page<Object> page = goodsRepository.countNewGoodsByCategory(fromDate, toDate, pageable);
        return page == null ? null : page.map(x -> (Object[]) x).map(x -> new UserCountByNameResponse(((Category) x[0]).getName(), (Long) x[1]));
    }
}
