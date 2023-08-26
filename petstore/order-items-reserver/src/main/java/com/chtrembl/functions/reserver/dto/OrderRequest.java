package com.chtrembl.functions.reserver.dto;


import java.util.Objects;

// Azure does not support records as POJO for requests
public final class OrderRequest {
    private final String sessionId;
    private final String payload;

    public OrderRequest(
            String sessionId,
            String payload
    ) {
        this.sessionId = sessionId;
        this.payload = payload;
    }

    public String sessionId() {
        return sessionId;
    }

    public String payload() {
        return payload;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (OrderRequest) obj;
        return Objects.equals(this.sessionId, that.sessionId) &&
                Objects.equals(this.payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, payload);
    }

    @Override
    public String toString() {
        return "OrderRequest[" +
                "sessionId=" + sessionId + ", " +
                "payload=" + payload + ']';
    }
}
