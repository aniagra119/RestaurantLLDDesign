package com.restaurant.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class OrderCancelledEvent implements KitchenEvent {
    private final String eventId;
    private final LocalDateTime timestamp;
    private final String orderId;
    private final String reason;

    public OrderCancelledEvent(String orderId, String reason) {
        this.eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 8);
        this.timestamp = LocalDateTime.now();
        this.orderId = Objects.requireNonNull(orderId, "Order ID cannot be null");
        this.reason = Objects.requireNonNull(reason, "Reason cannot be null");
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String getEventType() {
        return "ORDER_CANCELLED";
    }

    public String getOrderId() {
        return orderId;
    }

    public String getReason() {
        return reason;
    }
}

