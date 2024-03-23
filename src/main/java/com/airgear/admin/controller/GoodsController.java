package com.airgear.admin.controller;

import com.airgear.admin.dto.CountByNameDto;
import com.airgear.admin.dto.CountDto;
import com.airgear.admin.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/goods")
public class GoodsController {

    private GoodsService goodsService;

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR', 'USER')")
    @GetMapping("/count")
    public ResponseEntity<CountDto> getCountOfGoods() {
        return ResponseEntity.ok(goodsService.getCountOfGoods());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @GetMapping("/count/top")
    public ResponseEntity<CountDto> getCountOfTopGoods() {
        return ResponseEntity.ok(goodsService.getCountOfTopGoods());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR', 'USER')")
    @GetMapping("/count/new")
    public CountDto getCountOfNewGoods(@RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
                                       @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate) {
        return goodsService.getCountOfNewGoods(fromDate, toDate);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @GetMapping("/count/deleted")
    public ResponseEntity<CountByNameDto> getCountOfDeletedGoodsForCategory(
            @RequestParam String category,
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate) {
        return ResponseEntity.ok(goodsService.getCountOfDeletedGoods(fromDate, toDate, category));
    }


    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR', 'USER')")
    @GetMapping("/category/count")
    public ResponseEntity<Page<CountByNameDto>> getCountOfGoodsByCategory(Pageable pageable) {
        return ResponseEntity.ok(goodsService.getCountOfGoodsByCategory(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR', 'USER')")
    @GetMapping("/category/count/new")
    public ResponseEntity<Page<CountByNameDto>> getCountOfNewGoodsByCategory(
            @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fromDate,
            @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime toDate,
            Pageable pageable) {
        Page<CountByNameDto> categoryAmounts = goodsService.getCountOfNewGoodsByCategory(fromDate,toDate,pageable);
        return ResponseEntity.ok(categoryAmounts);
    }
}
