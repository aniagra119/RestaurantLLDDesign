package com.restaurant.event;

import com.restaurant.model.Order;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class OrderPlacedEvent implements KitchenEvent {
    private final String eventId;
    private final LocalDateTime timestamp;
    private final String sessionId;
    private final String tableNumber;
    private final Order order;

    public OrderPlacedEvent(String sessionId, String tableNumber, Order order) {
        this.eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 8);
        this.timestamp = LocalDateTime.now();
        this.sessionId = Objects.requireNonNull(sessionId, "Session ID cannot be null");
        this.tableNumber = tableNumber;
        this.order = Objects.requireNonNull(order, "Order cannot be null");
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
        return "ORDER_PLACED";
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public Order getOrder() {
        return order;
    }
}

