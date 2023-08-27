package com.chtrembl.petstoreapp.service;

public record ReserveOrderRequest(
        String sessionId,
        String payload
) {
}
