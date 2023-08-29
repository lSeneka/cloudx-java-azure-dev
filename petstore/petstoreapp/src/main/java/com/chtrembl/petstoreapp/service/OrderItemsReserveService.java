package com.chtrembl.petstoreapp.service;

import com.chtrembl.petstoreapp.model.ContainerEnvironment;
import com.chtrembl.petstoreapp.model.Order;
import com.chtrembl.petstoreapp.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class OrderItemsReserveService {

    private final ObjectMapper objectMapper;
    private final User sessionUser;
    private final ContainerEnvironment containerEnvironment;
    private WebClient reserveWebClient;

    @PostConstruct
    public void initialize() {
        this.reserveWebClient = WebClient.builder()
                .baseUrl(this.containerEnvironment.getOrderItemsReserveServiceURL())
                .build();
    }

    @SneakyThrows
    public void reserveOrder(Order order) {
        var orderJSON = this.objectMapper
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .writeValueAsString(order);

        var request = new ReserveOrderRequest(this.sessionUser.getSessionId(), orderJSON);
        this.reserveWebClient.post().uri("api/v1/orders")
                .body(BodyInserters.fromPublisher(Mono.just(request), ReserveOrderRequest.class))
                .accept(MediaType.APPLICATION_JSON)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .header("Cache-Control", "no-cache")
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}
