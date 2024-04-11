package com.airgear.admin.controller;

import com.airgear.admin.dto.UserCountByNameResponse;
import com.airgear.admin.dto.UserCountResponse;
import com.airgear.admin.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/goods")
public class GoodsController {

    private final GoodsService goodsService;

    @Autowired
    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR', 'USER')")
    @GetMapping("/count")
    public ResponseEntity<UserCountResponse> getCountOfGoods() {
        return ResponseEntity.ok(goodsService.getCountOfGoods());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @GetMapping("/count/top")
    public ResponseEntity<UserCountResponse> getCountOfTopGoods() {
        return ResponseEntity.ok(goodsService.getCountOfTopGoods());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR', 'USER')")
    @GetMapping("/count/new")
    public UserCountResponse getCountOfNewGoods(@RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
                                                @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate) {
        return goodsService.getCountOfNewGoods(fromDate, toDate);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @GetMapping("/count/deleted")
    public ResponseEntity<UserCountByNameResponse> getCountOfDeletedGoodsForCategory(
            @RequestParam(required = false) String category,
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate) {
        return ResponseEntity.ok(goodsService.getCountOfDeletedGoods(fromDate, toDate, category));
    }


    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR', 'USER')")
    @GetMapping("/category/count")
    public ResponseEntity<Page<UserCountByNameResponse>> getCountOfGoodsByCategory(Pageable pageable) {
        return ResponseEntity.ok(goodsService.getCountOfGoodsByCategory(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR', 'USER')")
    @GetMapping("/category/count/new")
    public ResponseEntity<Page<UserCountByNameResponse>> getCountOfNewGoodsByCategory(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate,
            Pageable pageable) {
        Page<UserCountByNameResponse> categoryAmounts = goodsService.getCountOfNewGoodsByCategory(fromDate,toDate,pageable);
        return ResponseEntity.ok(categoryAmounts);
    }
}
