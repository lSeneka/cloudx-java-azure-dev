package com.chtrembl.petstore.order.control;

import com.chtrembl.petstore.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CosmosDbService {
    private final OrdersRepository ordersRepository;

    public void save(Order order) {
        this.ordersRepository.save(order);
    }

    public Order findById(String id) {
        return this.ordersRepository
                .findById(id)
                .orElseGet(Order::new);
    }
}
