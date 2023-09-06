package com.chtrembl.petstore.product.control.mapping;

import com.chtrembl.petstore.product.entity.ProductEntity;
import com.chtrembl.petstore.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "status", source = "source")
    Product toProductModel(ProductEntity source);

    default Product.StatusEnum mapStatus(ProductEntity source) {
        return Product.StatusEnum.fromValue(source.getStatus());
    }
}
