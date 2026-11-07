package com.restaurant.event;

import com.restaurant.enums.OrderStatus;
import com.restaurant.model.Order;

import java.util.Objects;

public class OrderStatusChangedEvent extends OrderEvent {
    private final Order order;
    private final OrderStatus previousStatus;
    private final OrderStatus newStatus;

    public OrderStatusChangedEvent(String eventId, Order order, OrderStatus previousStatus, OrderStatus newStatus) {
        super(eventId);
        this.order = Objects.requireNonNull(order, "Order cannot be null");
        this.previousStatus = Objects.requireNonNull(previousStatus, "Previous status cannot be null");
        this.newStatus = Objects.requireNonNull(newStatus, "New status cannot be null");
    }

    public Order getOrder() {
        return order;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }
}
