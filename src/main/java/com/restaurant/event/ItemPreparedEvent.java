package com.restaurant.event;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ItemPreparedEvent implements KitchenEvent {
    private final String eventId;
    private final LocalDateTime timestamp;
    private final String orderId;
    private final String orderItemId;
    private final String chefId;

    public ItemPreparedEvent(String orderId, String orderItemId, String chefId) {
        this.eventId = "EVT-" + UUID.randomUUID().toString().substring(0, 8);
        this.timestamp = LocalDateTime.now();
        this.orderId = Objects.requireNonNull(orderId, "Order ID cannot be null");
        this.orderItemId = Objects.requireNonNull(orderItemId, "OrderItem ID cannot be null");
        this.chefId = Objects.requireNonNull(chefId, "Chef ID cannot be null");
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
        return "ITEM_PREPARED";
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderItemId() {
        return orderItemId;
    }

    public String getChefId() {
        return chefId;
    }
}

