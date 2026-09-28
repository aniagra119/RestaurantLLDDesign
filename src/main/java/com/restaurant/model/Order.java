package com.restaurant.model;

import com.restaurant.enums.OrderStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {
    private final String orderId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final long placedTimestamp;

    public Order(String orderId) {
        this.orderId = Objects.requireNonNull(orderId, "Order ID cannot be null");
        this.items = new ArrayList<>();
        this.status = OrderStatus.PLACED;
        this.placedTimestamp = Instant.now().toEpochMilli();
    }

    public String getOrderId() {
        return orderId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public long getPlacedTimestamp() {
        return placedTimestamp;
    }

    public synchronized void addItem(OrderItem item) {
        Objects.requireNonNull(item, "Order item cannot be null");
        if (status != OrderStatus.PLACED) {
            throw new IllegalStateException("Cannot add items to an order that is already " + status);
        }
        items.add(item);
    }

    public synchronized void updateStatus(OrderStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "Order status cannot be null");
    }

    public double calculateSubtotal() {
        return items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }
}
