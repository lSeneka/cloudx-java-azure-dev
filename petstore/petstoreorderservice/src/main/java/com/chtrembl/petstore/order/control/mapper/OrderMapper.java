package com.chtrembl.petstore.order.control.mapper;

import com.chtrembl.petstore.order.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    Order toOrderModel(com.chtrembl.petstore.order.entity.Order source);
}
