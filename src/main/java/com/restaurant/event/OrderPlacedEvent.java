package com.restaurant.event;

import com.restaurant.model.CustomerVisit;
import com.restaurant.model.Order;

import java.util.Objects;

public class OrderPlacedEvent extends OrderEvent {
    private final CustomerVisit visit;
    private final Order order;

    public OrderPlacedEvent(String eventId, CustomerVisit visit, Order order) {
        super(eventId);
        this.visit = Objects.requireNonNull(visit, "Visit cannot be null");
        this.order = Objects.requireNonNull(order, "Order cannot be null");
    }

    public CustomerVisit getVisit() {
        return visit;
    }

    public Order getOrder() {
        return order;
    }
}
