package com.airgear.admin.controller;

import com.airgear.admin.dto.GoodsDto;
import com.airgear.admin.dto.UserCountByNameResponse;
import com.airgear.admin.dto.UserCountResponse;
import com.airgear.admin.service.GoodsService;
import com.airgear.admin.utils.Routes;
import com.airgear.model.Goods;
import com.airgear.model.GoodsCondition;
import com.airgear.model.GoodsStatus;
import com.airgear.model.GoodsVerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    @GetMapping
    public ResponseEntity<Page<GoodsDto>> getGoods(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String locationName,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) GoodsVerificationStatus verificationStatus,
            @RequestParam(required = false) GoodsStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startCreatedAt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endCreatedAt,
            @RequestParam(required = false) GoodsCondition goodsCondition
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Specification<Goods> spec = createSpecification(name, minPrice, maxPrice, category, locationName, userName,
                verificationStatus, status, startCreatedAt, endCreatedAt, goodsCondition);
        Page<GoodsDto> goods = goodsService.getGoods(spec, pageRequest);
        return ResponseEntity.ok(goods);
    }

    private Specification<Goods> createSpecification(String name, BigDecimal minPrice, BigDecimal maxPrice, String category,
                                                     String locationName, String userName, GoodsVerificationStatus verificationStatus,
                                                     GoodsStatus status, OffsetDateTime start, OffsetDateTime end,
                                                     GoodsCondition condition) {

        return Specification.where(nameContains(name))
                .and(priceGreaterThan(minPrice))
                .and(priceLesserThan(maxPrice))
                .and(categoryIs(category))
                .and(locationIs(locationName))
                .and(userNameIs(userName))
                .and(verificationStatusIs(verificationStatus))
                .and(statusIs(status))
                .and(createdAtBetween(start, end))
                .and(goodsConditionIs(condition));
    }

    private Specification<Goods> nameContains(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    private Specification<Goods> userNameIs(String userName) {
        return (root, query, criteriaBuilder) ->
                userName == null ? null : criteriaBuilder.equal(root.join("user").get("name"), userName.trim());
    }


    private Specification<Goods> categoryIs(String categoryName) {
        return (root, query, criteriaBuilder) -> {
            if (categoryName == null || categoryName.trim().isEmpty()) {
                return null;
            } else {
                return criteriaBuilder.equal(root.join("category").get("name"), categoryName.trim());
            }
        };
    }

    private Specification<Goods> priceGreaterThan(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) ->
                minPrice == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("price").get("priceAmount"), minPrice);
    }

    private Specification<Goods> priceLesserThan(BigDecimal minPrice) {
        return (root, query, criteriaBuilder) ->
                minPrice == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("price").get("priceAmount"), minPrice);
    }

    private Specification<Goods> locationIs(String locationName) {
        return (root, query, criteriaBuilder) ->
                locationName == null ? null : criteriaBuilder.equal(root.join("location").get("settlement"), locationName.trim());
    }

    private Specification<Goods> verificationStatusIs(GoodsVerificationStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("verificationStatus"), status);
    }

    private Specification<Goods> statusIs(GoodsStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    private Specification<Goods> goodsConditionIs(GoodsCondition condition) {
        return (root, query, criteriaBuilder) ->
                condition == null ? null : criteriaBuilder.equal(root.get("goodsCondition"), condition);
    }

    private Specification<Goods> createdAtBetween(OffsetDateTime start, OffsetDateTime end) {
        return (root, query, criteriaBuilder) ->
                start == null || end == null ? null : criteriaBuilder.between(root.get("createdAt"), start, end);
    }
}