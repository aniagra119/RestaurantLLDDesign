package com.restaurant.model;

import com.restaurant.enums.OrderChannel;
import com.restaurant.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {
    private final String id;
    private final OrderChannel channel;
    private final LocalDateTime placedAt;
    private OrderStatus status;
    private final List<OrderItem> items;

    public Order(String id, OrderChannel channel) {
        this.id = Objects.requireNonNull(id, "Order ID cannot be null");
        this.channel = Objects.requireNonNull(channel, "OrderChannel cannot be null");
        this.placedAt = LocalDateTime.now();
        this.status = OrderStatus.RECEIVED;
        this.items = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public OrderChannel getChannel() {
        return channel;
    }

    public LocalDateTime getPlacedAt() {
        return placedAt;
    }

    public synchronized OrderStatus getStatus() {
        return status;
    }

    public synchronized void updateStatus(OrderStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "OrderStatus cannot be null");
    }

    public synchronized void addItem(OrderItem item) {
        Objects.requireNonNull(item, "OrderItem cannot be null");
        if (status != OrderStatus.RECEIVED) {
            throw new IllegalStateException("Cannot add items to an order that is already " + status);
        }
        this.items.add(item);
    }

    public synchronized List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public synchronized BigDecimal calculateSubtotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getSubtotal());
        }
        return total;
    }
}

