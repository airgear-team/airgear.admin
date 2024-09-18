package com.airgear.admin.controller;

import com.airgear.admin.dto.UserCountByNameResponse;
import com.airgear.admin.dto.UserCountResponse;
import com.airgear.admin.service.GoodsService;
import com.airgear.admin.utils.Routes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(Routes.GOODS)
public class GoodsController {

    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping(value = "/count",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserCountResponse getCountOfGoods() {
        return goodsService.getCountOfGoods();
    }

    @GetMapping(
            value = "/count/top",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserCountResponse getCountOfTopGoods() {
        return goodsService.getCountOfTopGoods();
    }

    @GetMapping(
            value = "/count/new",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserCountResponse getCountOfNewGoods(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate) {
        return goodsService.getCountOfNewGoods(fromDate, toDate);
    }

    @GetMapping(
            value = "/count/deleted",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserCountByNameResponse getCountOfDeletedGoodsForCategory(
            @RequestParam(required = false) String category,
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate) {
        return goodsService.getCountOfDeletedGoods(fromDate, toDate, category);
    }

    @GetMapping(
            value = "/count/category",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Page<UserCountByNameResponse> getCountOfGoodsByCategory(Pageable pageable) {
        return goodsService.getCountOfGoodsByCategory(pageable);
    }

    @GetMapping(
            value = "/count/category/new",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Page<UserCountByNameResponse> getCountOfNewGoodsByCategory(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate,
            Pageable pageable) {
        return goodsService.getCountOfNewGoodsByCategory(fromDate, toDate, pageable);
    }

}