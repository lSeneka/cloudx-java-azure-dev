package com.chtrembl.petstore.order.control.mapper;

import com.chtrembl.petstore.order.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    com.chtrembl.petstore.order.entity.Order.Product toProductEntity(Product source);
}
