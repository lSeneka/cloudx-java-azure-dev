package com.chtrembl.petstore.order.entity;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Container(containerName = "orders")
public class Order {
    @Id
    @PartitionKey
    private String id;

    private String email;

    private List<Product> products = new ArrayList<>();

    private OffsetDateTime shipDate;

    private List<Tag> tags;

    private String status;

    private Boolean complete;

    @Getter
    @Setter
    public static class Product {
        private Long id;
        private Integer quantity;
        private String name;
        private String photoURL;
    }

    public record Tag(
            Long id,
            String name
    ) {
    }
}

