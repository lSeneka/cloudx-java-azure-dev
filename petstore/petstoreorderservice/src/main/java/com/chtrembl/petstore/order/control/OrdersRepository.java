package com.chtrembl.petstore.order.control;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.chtrembl.petstore.order.entity.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends CosmosRepository<Order, String> {
}
