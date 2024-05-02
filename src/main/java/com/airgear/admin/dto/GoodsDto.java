package com.airgear.admin.dto;

import com.airgear.model.GoodsCondition;
import com.airgear.model.GoodsStatus;
import com.airgear.model.GoodsVerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class GoodsDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal weekendsPrice;
    private String location;
    private BigDecimal deposit;
    private String userName;
    private GoodsVerificationStatus verificationStatus;
    private GoodsStatus status;
    private OffsetDateTime createdAt;
    private int goodsViews;
    private GoodsCondition goodsCondition;
}
