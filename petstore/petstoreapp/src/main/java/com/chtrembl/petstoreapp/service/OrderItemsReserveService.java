package com.chtrembl.petstoreapp.service;

import com.chtrembl.petstoreapp.model.ContainerEnvironment;
import com.chtrembl.petstoreapp.model.User;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Service
public class OrderItemsReserveService {

    private final User sessionUser;
    private final ContainerEnvironment containerEnvironment;
    private WebClient reserveWebClient;

    public OrderItemsReserveService(User sessionUser, ContainerEnvironment containerEnvironment) {
        this.sessionUser = sessionUser;
        this.containerEnvironment = containerEnvironment;
    }

    @PostConstruct
    public void initialize() {
        this.reserveWebClient = WebClient.builder()
                .baseUrl(this.containerEnvironment.getOrderItemsReserveServiceURL())
                .build();
    }

    public void reserveOrder(String order) {
        var request = new ReserveOrderRequest(this.sessionUser.getSessionId(), order);
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
