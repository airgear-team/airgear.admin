package com.airgear.admin.mapper;

import com.airgear.admin.dto.GoodsDto;
import com.airgear.model.Goods;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface GoodsMapper {

    @Mapping(source = "price.priceAmount", target = "price")
    @Mapping(source = "weekendsPrice.weekendsPriceAmount", target = "weekendsPrice")
    @Mapping(source = "location.settlement", target = "location")
    @Mapping(source = "deposit.amount", target = "deposit")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "goodsViews", target = "goodsViews", qualifiedByName = "size")
    GoodsDto goodsToGoodsDto(Goods goods);

    @Named("size")
    default long mapCollectionSize(Collection<?> collection) {
        return collection != null ? collection.size() : 0;
    }
}
