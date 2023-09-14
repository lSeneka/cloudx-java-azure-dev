package com.chtrembl.petstoreapp.service;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.chtrembl.petstoreapp.model.Order;
import com.chtrembl.petstoreapp.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemsReserveService {

    private final ObjectMapper objectMapper;
    private final User sessionUser;
    private final ServiceBusSenderClient senderClient;

    @SneakyThrows
    public void reserveOrder(Order order) {
        var orderJSON = this.objectMapper
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .writeValueAsString(order);

        var request = new ReserveOrderRequest(this.sessionUser.getSessionId(), orderJSON);
        var message = new ServiceBusMessage(BinaryData.fromObject(request));
        this.senderClient.sendMessage(message);
    }
}
