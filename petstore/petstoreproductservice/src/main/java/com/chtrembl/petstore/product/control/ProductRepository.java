package com.chtrembl.petstore.product.control;

import com.chtrembl.petstore.product.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}